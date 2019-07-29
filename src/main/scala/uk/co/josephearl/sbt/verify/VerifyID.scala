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

import scala.util.Try
import scala.xml.{Elem, XML}


final case class VerifyID(organization: String,
                          name: String,
                          hash: String,
                          algorithm: HashAlgorithm = HashAlgorithm.SHA1) extends Ordered[VerifyID] {

  def asSbtSettingString: String = "\"%s\" %% \"%s\" %s \"%s\"".format(organization.replace("\\", "\\\\"), name, algorithm.algorithm, hash)

  override def compare(that: VerifyID): Int = this.toString compare that.toString
}

object VerifyID {
  def fromFile(file: File, algorithm: HashAlgorithm, projectBase: File): VerifyID = {
    VerifyID(
      toOrganization(file, projectBase.getAbsolutePath),
      toName(file),
      algorithm.fileContentHash(file),
      algorithm
    )
  }

  private def toOrganization(jarFile: File, projectBase: String): String = {
    val parentPath = jarFile.getParentFile.getAbsolutePath
    inPomFile(jarFile, pomOrganization)
      .orElse(inIvyCache(parentPath, ivyCacheOrganization(parentPath, _)))
      .getOrElse(Paths.get(projectBase).relativize(Paths.get(parentPath)).toString)
  }

  private def toName(jarFile: File): String = {
    val parentPath = jarFile.getParentFile.getAbsolutePath
    inPomFile(jarFile, pomName)
      .orElse(inIvyCache(parentPath, ivyCacheName(parentPath, _)))
      .getOrElse(filenameName(jarFile.getName))
  }


  private def inIvyCache(path: String, in: Int => String): Option[String] = {
    val ivyCacheDir: String = s"ivy2${File.separator}cache${File.separator}"
    path.lastIndexOf(ivyCacheDir) match {
      case -1     => None
      case x: Int => Some(in(x + ivyCacheDir.length))
    }
  }

  private def ivyCacheOrganization(path: String, x: Int): String = {
    path.substring(x, path.indexOf(File.separator, x))
  }

  private def ivyCacheName(path: String, x: Int): String = {
    path.substring(path.indexOf(File.separator, x) + 1, path.indexOf(File.separator, path.indexOf(File.separator, x) + 1))
      .replaceFirst("_(\\d+\\.)?(\\d+\\.)?(\\d+)$", "")
  }

  // Coursier generates a POM file for every dependency, so we first check for a POM file before checking the IVY cache
  private def inPomFile(jarFile: File, in: Elem => Option[String]): Option[String] = {
    jarFile
      .getParentFile
      .listFiles()
      .find(_.getName.endsWith(".pom"))
      .flatMap(pomFile => Try(XML.loadFile(pomFile)).toOption)
      .flatMap(in)
  }

  private def pomOrganization(pomXml: Elem): Option[String] = {
    // Sub-projects don't have their own "groupId" and inherit this from their "parent"
    (pomXml \ "groupId")
      .headOption
      .orElse((pomXml \ "parent" \ "groupId").headOption)
      .map(_.text)
  }

  private def pomName(pomXml: Elem): Option[String] = {
    (pomXml \ "artifactId")
      .headOption
      .map(_.text)
  }


  private def filenameName(filename: String): String = {
    stripVersion(stripExtension(filename))
  }

  private def stripExtension(filename: String): String = filename.lastIndexOf('.') match {
    case -1     => filename
    case x: Int => filename.substring(0, x)
  }

  private def stripVersion(filename: String): String = filename.replaceFirst("-(\\d+\\.)?(\\d+\\.)?(\\d+)$", "")
}