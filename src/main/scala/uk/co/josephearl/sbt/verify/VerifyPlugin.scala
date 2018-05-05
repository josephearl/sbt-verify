/*
 * Copyright (c) 2015-2016 Joseph Earl & contributors.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the under the Apache License,
 * Version 2.0 which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package uk.co.josephearl.sbt.verify

import sbt.Def.Initialize
import sbt.Keys._
import sbt._

object VerifyPlugin extends AutoPlugin {

  object autoImport extends VerifyKeys {
    def verifyTask(key: TaskKey[Unit]): Initialize[Task[Unit]] = VerifyPlugin.verifyTask(key)

    def verifyGenerateTask(key: TaskKey[File]): Initialize[Task[File]] = VerifyPlugin.verifyGenerateTask(key)

    def verifyJarsTask[T](key: TaskKey[T]): Initialize[Task[Seq[File]]] = VerifyPlugin.verifyJarsTask(key)

    implicit def verifyToRichGroupArtifactID(g: sbt.librarymanagement.DependencyBuilders.OrganizationArtifactName): RichGroupArtifactID = RichGroupArtifactID.from(g)

    implicit def verifyToRichGroupArtifactID(m: sbt.ModuleID): RichGroupArtifactID = RichGroupArtifactID.from(m)

    val VerifyOptions = uk.co.josephearl.sbt.verify.VerifyOptions
    val VerifyID = uk.co.josephearl.sbt.verify.VerifyID
    val baseVerifySettings = VerifyPlugin.baseVerifySettings
  }

  import autoImport.{baseVerifySettings => _, verifyJarsTask => _, verifyTask => _, _}

  def verifyTask(key: TaskKey[Unit]): Initialize[Task[Unit]] = Def.task {
    val s = (streams in key).value
    Verify.verify(
      (verifyJars in key).value,
      (verifyDependencies in key).value,
      (verifyOptions in key).value,
      s.log)
  }

  def verifyGenerateTask(key: TaskKey[File]): Initialize[Task[File]] = Def.task {
    val s = (streams in key).value
    Verify.verifyGenerate(
      (verifyJars in key).value,
      (verifyOutputFile in key).value,
      (verifyAlgorithm in key).value,
      (verifyOptions in key).value,
      baseDirectory.value,
      s.log)
  }

  def verifyJarsTask[T](key: TaskKey[T]): Initialize[Task[Seq[File]]] = Def.task {
    val s = (streams in key).value
    Verify.verifyJars(
      (fullClasspath in key).value,
      (externalDependencyClasspath in key).value,
      (verifyOptions in key).value,
      s.log)
  }

  lazy val baseVerifySettings: Seq[Def.Setting[_]] = Seq(
    // verify
    verify := verifyTask(verify).value,
    verifyDependencies in verify := Nil,
    verifyJars in verify := verifyJarsTask(verify).value,
    verifyOptions in verify := VerifyOptions(
      includeBin = true,
      includeScala = true,
      includeDependency = true,
      excludedJars = Nil
    ),
    fullClasspath in verify := (fullClasspath or (fullClasspath in Runtime)).value,
    externalDependencyClasspath in verify := (externalDependencyClasspath or (externalDependencyClasspath in Runtime)).value,

    // verifyGenerate
    verifyGenerate := verifyGenerateTask(verifyGenerate).value,
    verifyOutputFile in verifyGenerate := crossTarget(_ / "verify.sbt").value,
    verifyAlgorithm in verifyGenerate := HashAlgorithm.SHA1,
    verifyJars in verifyGenerate := verifyJarsTask(verifyGenerate).value,
    verifyOptions in verifyGenerate := VerifyOptions(
      includeBin = true,
      includeScala = true,
      includeDependency = true,
      excludedJars = Nil
    ),
    fullClasspath in verifyGenerate := (fullClasspath or (fullClasspath in Runtime)).value,
    externalDependencyClasspath in verifyGenerate := (externalDependencyClasspath or (externalDependencyClasspath in Runtime)).value
  )

  override def requires = sbt.plugins.JvmPlugin

  override def trigger = allRequirements

  override lazy val projectSettings: Seq[Def.Setting[_]] = baseVerifySettings
}
