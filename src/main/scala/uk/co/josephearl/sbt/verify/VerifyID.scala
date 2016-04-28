/*
 * Copyright (c) 2015-2016 Joseph Earl & contributors.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the under the Apache License,
 * Version 2.0 which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package uk.co.josephearl.sbt.verify

import java.io.File
import java.nio.file.Paths

import uk.co.josephearl.sbt.verify.HashAlgorithm.HashAlgorithm
import sbt.{ModuleID, Logger}

final case class VerifyID(module: sbt.ModuleID, hash: String, algorithm: HashAlgorithm = HashAlgorithm.SHA1) {
  def algorithm(algorithm: String): VerifyID = this.copy(algorithm = HashAlgorithm.withName(algorithm))

  def algorithm(algorithm: HashAlgorithm): VerifyID = this.copy(algorithm = algorithm)

  def shouldVerifyFile(file: File, log: Logger): Boolean = {
    matchesOrganization(file) && matchesName(file) && matchesRevision(file)
  }

  def verifyFile(file: File, options: VerifyOptions, log: Logger): Boolean = {
    val actual = algorithm.fileContentHash(file).toLowerCase
    val expected = hash.toLowerCase
    val matches = actual == expected
    // TODO: option to not throw error here?
    matches match {
      case false  => throw VerifyException(s"Hash '$actual' did not match expected '$expected' using algorithm '${algorithm.algorithm}' for file '${file.getAbsolutePath}'")
      case true   => true
    }
  }

  def asSbtSettingString: String = "\"%s\" %% \"%s\" %% \"%s\" %s \"%s\"".format(module.organization, module.name, module.revision, algorithm.algorithm, hash)

  private def matchesOrganization(file: File): Boolean = {
    file.getAbsolutePath.contains(module.organization) ||
      (VerifyUtils.isScalaLibraryFile(file) && module.organization == "org.scala-lang" && file.getAbsolutePath.contains("/global/boot/"))
  }

  private def matchesName(file: File): Boolean = {
    file.getName.contains(module.name)
  }

  private def matchesRevision(file: File): Boolean = {
    file.getName.contains(module.revision) || file.getAbsolutePath.contains(module.revision)
  }
}

object VerifyID {
  def fromFile(file: File, algorithm: HashAlgorithm, projectBase: File): VerifyID = {
    val path = file.getParentFile.getAbsolutePath
    val filename = file.getName
    VerifyID(
      ModuleID(toOrganization(path, projectBase.getAbsolutePath), toName(path, filename), toRevision(path, filename)),
      algorithm.fileContentHash(file),
      algorithm
    )
  }

  private def toOrganization(path: String, projectBase: String): String = {
    inIvyCache(path, ivyCacheOrganization(path, _))
      .getOrElse(Paths.get(projectBase).relativize(Paths.get(path)).toString)
  }

  private def toName(path: String, filename: String): String = {
    inIvyCache(path, ivyCacheName(path, _))
      .getOrElse(filenameName(filename))
  }

  private def toRevision(path: String, filename: String): String = {
    inIvyCache(path, _ => ivyCacheRevision(filename))
      .getOrElse(filenameRevision(filename))
  }

  private def inIvyCache(path: String, in: Int => String): Option[String] = {
    val ivyCacheDir: String = "ivy2/cache/"
    path.lastIndexOf(ivyCacheDir) match {
      case -1     => None
      case x: Int => Some(in(x + ivyCacheDir.length))
    }
  }

  private def ivyCacheOrganization(path: String, x: Int): String = {
    path.substring(x, path.indexOf('/', x))
  }

  private def ivyCacheName(path: String, x: Int): String = {
    path.substring(path.indexOf('/', x) + 1, path.indexOf('/', path.indexOf('/', x) + 1))
      .replaceFirst("_(\\d+\\.)?(\\d+\\.)?(\\d+)$", "")
  }

  private def ivyCacheRevision(filename: String): String = {
    stripExtension(filename).substring(stripExtension(filename).lastIndexOf('-') + 1)
  }

  private def filenameName(filename: String): String = {
    stripVersion(stripExtension(filename))
  }

  private def filenameRevision(filename: String): String = stripExtension(filename).lastIndexOf('-') match {
    case -1 => ""
    case _  => ivyCacheRevision(filename)
  }

  private def stripExtension(filename: String): String = filename.lastIndexOf('.') match {
    case -1     => filename
    case x: Int => filename.substring(0, x)
  }

  private def stripVersion(filename: String): String = filename.replaceFirst("-(\\d+\\.)?(\\d+\\.)?(\\d+)$", "")
}