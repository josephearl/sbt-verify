/*
 * Copyright (c) 2015-2016 Joseph Earl & contributors.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the under the Apache License,
 * Version 2.0 which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package uk.co.josephearl.sbt.verify

import sbt._
import uk.co.josephearl.sbt.verify.HashAlgorithm.HashAlgorithm

trait VerifyKeys {
  // Tasks
  lazy val verify = TaskKey[Unit]("verify")
  lazy val verifyGenerate = TaskKey[File]("verifyGenerate")

  // Common keys
  lazy val verifyJars = TaskKey[Seq[File]]("verifyJars")
  lazy val verifyOptions = SettingKey[VerifyOptions]("verifyOptions")
  lazy val verifyAlgorithm = SettingKey[HashAlgorithm]("verifyAlgorithm")

  // Settings for verify task
  lazy val verifyDependencies = SettingKey[Seq[VerifyID]]("verifyDependencies")

  // Settings for verifyGenerate task
  lazy val verifyGenerateOutputFile = SettingKey[File]("verifyGenerateOutputFile")

  /*
   * These implicits take care of converting the verifyDependencies to the appropriate VerifyID object.
   *
   * For example, consider that the dependencies are defined as below:
   * verifyDependencies in verify := Seq(
   *   "ch.qos.logback" % "logback-classic" MD5 "64f7a68f931aed8e5ad8243470440f0b",
   *   "ch.qos.logback" % "logback-core" % 1.0.0 MD5 "841fc80c6edff60d947a3872a2db4d45"
   * )
   *
   * SBT creates the following objects from these:
   * OrganizationArtifactName(organization = "ch.qos.logback", name = "logback-classic")
   * ModuleID(organization = "ch.qos.logback", name = "logback-core", revision = 1.0.0)
   *
   * These are then implicitly converted to RichGroupArtifactID objects using the below implicits.
   *
   * The next string MD5 or SHA1 are actually functions invoked on the RichGroupArtifactID objects which convert
   * these to the corresponding VerifyID objects.
   */
  implicit def verifyToRichGroupArtifactID(g: sbt.librarymanagement.DependencyBuilders.OrganizationArtifactName): RichGroupArtifactID = RichGroupArtifactID.from(g)
  implicit def verifyToRichGroupArtifactID(m: sbt.ModuleID): RichGroupArtifactID = RichGroupArtifactID.from(m)

  // Define these under autoImport to have these data types / values available to the projects using this plugin
  val VerifyOptions = uk.co.josephearl.sbt.verify.VerifyOptions
  val VerifyID = uk.co.josephearl.sbt.verify.VerifyID
  val baseVerifySettings = VerifyPlugin.baseVerifySettings
  val HashAlgorithm = uk.co.josephearl.sbt.verify.HashAlgorithm
}
