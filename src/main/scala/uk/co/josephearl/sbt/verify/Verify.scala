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

import scala.util.{Success, Try}

object Verify {
  def verify(jars: Seq[File], verifications: Seq[VerifyID],
             options: VerifyOptions, log: Logger): Unit = {
    var remaining: Seq[VerifyID] = verifications
    val verified: Seq[Tuple2[File, VerifyID]] = jars.flatMap(f => {
      val matchingVerifications = remaining.filter(_.shouldVerifyFile(f, log))
      val successVerifications = matchingVerifications.map { mv =>
        Try {
          mv.verifyFile(f, options, log)
          mv
        }
      }.collect { case Success(mv) => mv }

      successVerifications.headOption.map { verifyId =>
        remaining = verifications diff Seq(verifyId)
        (f, verifyId)
      }
    })

    if (verified.size != jars.size) {
      val unused = "Files not verified (no matching verification found):\n" +
        jars.filterNot(verified.map(_._1).toSet).map(fileHashString).mkString("\n") + "\n" +
        "Verifications:\n" +
        verified.map(_._2).map(_.asSbtSettingString).mkString("\n")
      log.warn(unused)

      val message = s"${jars.size - verified.size} unverified files\n"
      options.warnOnUnverifiedFiles match {
        case true => log.warn(message)
        case false => sys.error(message)
      }
    }

    if (verified.size != verifications.size) {
      val unused = "Verifications not used (no files matching verification found):\n" +
        verifications.filterNot(verified.map(_._2).toSet).map(_.toString).mkString("\n")
      log.warn(unused)

      val message = s"${verifications.size - verified.size} unused verifications"
      options.warnOnUnusedVerifications match {
        case true => log.warn(message)
        case false => sys.error(message)
      }
    }
  }

  def verifyGenerate(jars: Seq[File], outputFile: File, algorithm: HashAlgorithm,
                     options: VerifyOptions, projectBase: File, log: Logger): File = {
    val verifications: Seq[VerifyID] = jars.map(VerifyID.fromFile(_, algorithm, projectBase))
    val content = "verifyDependencies in verify ++= Seq(\n" +
      verifications.map(v => "  %s".format(v.asSbtSettingString)).mkString(",\n") +
      "\n)\n\n" +
      "verifyOptions in verify := VerifyOptions(\n" +
      "  includeBin = %s,\n".format(options.includeBin) +
      "  includeScala = %s,\n".format(options.includeScala) +
      "  includeDependency = %s,\n".format(options.includeDependency) +
      "  excludedJars = %s,\n".format(options.excludedJars match {
        case Nil => "Nil"
        case x: Classpath => "Seq(" + x.map(_.data.getPath).map("File(%s)".format(_)).mkString(", ") + ")"
      }) +
      "  warnOnUnverifiedFiles = %s,\n".format(options.warnOnUnverifiedFiles) +
      "  warnOnUnusedVerifications = %s\n".format(options.warnOnUnusedVerifications) +
      ")\n"
    log.debug("Writing verification file:" + System.lineSeparator + content)
    write(outputFile, content)
  }

  def verifyJars(classpath: Classpath, dependencies: Classpath,
                 options: VerifyOptions, log: Logger): Seq[File] = {
    import sbt.internal.inc.classpath.ClasspathUtilities

    val (libs, dirs) = classpath.toVector.partition(c => ClasspathUtilities.isArchive(c.data))

    val depLibs = dependencies.map(_.data).toSet.filter(ClasspathUtilities.isArchive)
    val excludedJars = options.excludedJars map {
      _.data
    }
    val jars = libs flatMap {
      case jar if excludedJars contains jar.data.asFile => None
      case jar if VerifyUtils.isScalaLibraryFile(jar.data.asFile) =>
        if (options.includeScala) Some(jar) else None
      case jar if depLibs contains jar.data.asFile =>
        if (options.includeDependency) Some(jar) else None
      case jar =>
        if (options.includeBin) Some(jar) else None
    }
    jars map (_.data)
  }

  private def fileHashString(file: File): String = {
    "%s %s %s".format(file.getAbsolutePath, HashAlgorithm.SHA1.algorithm, HashAlgorithm.SHA1.fileContentHash(file))
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
