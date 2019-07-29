/*
 * Copyright (c) 2015-2016 Joseph Earl & contributors.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the under the Apache License,
 * Version 2.0 which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package uk.co.josephearl.sbt.verify

import java.io.{File, PrintWriter}

import sbt.Keys.Classpath
import sbt._
import uk.co.josephearl.sbt.verify.HashAlgorithm.HashAlgorithm

object Verify {
  def verify(jars: Seq[File],
             verifications: Seq[VerifyID],
             algorithm: HashAlgorithm,
             options: VerifyOptions,
             projectBase: File,
             log: VerifyLogger): Unit = {
    log.info(s"Verifying [${jars.length}] libraries")

    val currentJars = jars
      .map(VerifyID.fromFile(_, algorithm, projectBase))
      .distinct
      .sorted

    val verifiedJars = verifications
      .distinct
      .sorted

    val missingVerifications = currentJars diff verifiedJars
    if (missingVerifications.nonEmpty) {
      log.warn(s"Files not verified (no matching verifications found): \n${missingVerifications.mkString("\n")}\n")
      val message1 = log.mkMessage(s"${missingVerifications.size} unverified files\n")
      if (options.warnOnUnverifiedFiles) log.warn(message1)
      else sys.error(message1)
    }
    val extraVerifications = verifiedJars diff currentJars
    if (extraVerifications.nonEmpty) {
      log.warn(s"Verifications not used (no files matching verification found): \n${extraVerifications.mkString("\n")}\n")
      val message2 = log.mkMessage(s"${missingVerifications.size} unverified files\n")
      if (options.warnOnUnusedVerifications) log.warn(message2)
      else sys.error(message2)
    }

    log.info("Verification successful")
  }

  def verifyGenerate(jars: Seq[File],
                     outputFile: File,
                     algorithm: HashAlgorithm,
                     options: VerifyOptions,
                     projectBase: File,
                     log: VerifyLogger): File = {
    log.info(s"Generating verify file for [${jars.length}] libraries to [${outputFile.getAbsolutePath}]")

    val verifications = jars
      .map(VerifyID.fromFile(_, algorithm, projectBase))
      .distinct
      .sorted

    val verifyDepStr = "verifyDependencies in verify ++= Seq(\n" +
      verifications.map(v => "  %s".format(v.asSbtSettingString)).mkString(",\n") + "\n)\n\n"
    val verifyOptStr = "verifyOptions in verify := VerifyOptions(\n" +
      "  includeBin = %s,\n".format(options.includeBin) +
      "  includeScala = %s,\n".format(options.includeScala) +
      "  includeDependency = %s,\n".format(options.includeDependency) +
      "  excludedJars = %s,\n".format(options.excludedJars match {
        case Nil => "Nil"
        case x: Classpath => "Seq(" + x.map(_.data.getPath).map("File(%s)".format(_)).mkString(", ") + ")"
      }) +
      "  warnOnUnverifiedFiles = %s,\n".format(options.warnOnUnverifiedFiles) +
      "  warnOnUnusedVerifications = %s\n".format(options.warnOnUnusedVerifications) +
      ")\n\n"
    val verifyAlgoStr = s"verifyAlgorithm in verify := HashAlgorithm.$algorithm\n"

    val content = verifyDepStr + verifyOptStr + verifyAlgoStr
    log.debug("Writing verification file:" + System.lineSeparator + content)
    write(outputFile, content)
  }

  def verifyJars(classpath: Classpath,
                 dependencies: Classpath,
                 options: VerifyOptions,
                 log: VerifyLogger): Seq[File] = {
    import sbt.internal.inc.classpath.ClasspathUtilities

    val libs = classpath
      .map(_.data)
      .distinct
      .filter(ClasspathUtilities.isArchive)

    val depLibs = dependencies
      .map(_.data)
      .distinct
      .filter(ClasspathUtilities.isArchive)

    val excludedJars = options
      .excludedJars
      .map(_.data)

    libs flatMap {
      case jar if excludedJars contains jar.asFile =>
        None
      case jar if VerifyUtils.isScalaLibraryFile(jar.asFile) =>
        if (options.includeScala) Some(jar) else None
      case jar if depLibs contains jar.asFile =>
        if (options.includeDependency) Some(jar) else None
      case jar =>
        if (options.includeBin) Some(jar) else None
    }
  }

  private def write(file: File, content: String): File = {
    var writer: PrintWriter = null
    var t: Throwable = null
    try {
      writer = new PrintWriter(file)
      writer.println(content)
    } catch {
      case x: Throwable => t = x; throw x
    } finally {
      if (writer != null) {
        if (t != null) {
          try {
            writer.close()
          } catch {
            case y: Throwable => t.addSuppressed(y)
          }
        } else {
          writer.close()
        }
      }
    }
    file
  }
}
