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
  val pluginName = "sbt-verify"

  object autoImport extends VerifyKeys

  import autoImport._

  def verifyTask(key: TaskKey[Unit]): Initialize[Task[Unit]] = Def.task {
    val time0 = System.nanoTime
    val s = (streams in key).value
    val logger = new VerifyLogger(s.log, pluginName, baseDirectory.value.getName)
    val output = Verify.verify(
      (verifyJars in key).value,
      (verifyDependencies in key).value,
      (verifyAlgorithm in key).value,
      (verifyOptions in key).value,
      baseDirectory.value,
      logger)

    val duration = (System.nanoTime - time0) / 1e9d
    logger.info(s"Time taken to verify: [$duration seconds]")
    output
  }

  def verifyGenerateTask(key: TaskKey[File]): Initialize[Task[File]] = Def.task {
    val time0 = System.nanoTime
    val s = (streams in key).value
    val logger = new VerifyLogger(s.log, pluginName, baseDirectory.value.getName)
    val output = Verify.verifyGenerate(
      (verifyJars in key).value,
      (verifyGenerateOutputFile in key).value,
      (verifyAlgorithm in key).value,
      (verifyOptions in key).value,
      baseDirectory.value,
      logger)

    val duration = (System.nanoTime - time0) / 1e9d
    logger.info(s"Time taken to verifyGenerate: [$duration seconds]")
    output
  }

  def verifyJarsTask[T](key: TaskKey[T]): Initialize[Task[Seq[File]]] = Def.task {
    val s = (streams in key).value
    val logger = new VerifyLogger(s.log, pluginName, baseDirectory.value.getName)
    Verify.verifyJars(
      (fullClasspath in key).value,
      (externalDependencyClasspath in key).value,
      (verifyOptions in key).value,
      logger)
  }

  lazy val baseVerifySettings: Seq[Def.Setting[_]] = Seq(
    // verify
    verify := verifyTask(verify).value,
    verifyDependencies in verify := Nil,
    verifyAlgorithm in verify := HashAlgorithm.SHA1,
    verifyJars in verify := verifyJarsTask(verify).value,
    verifyOptions in verify := VerifyOptions(),
    fullClasspath in verify := (fullClasspath or (fullClasspath in Runtime)).value,
    externalDependencyClasspath in verify := (externalDependencyClasspath or (externalDependencyClasspath in Runtime)).value,

    // verifyGenerate
    verifyGenerate := verifyGenerateTask(verifyGenerate).value,
    verifyGenerateOutputFile in verifyGenerate := baseDirectory(_ / "verify.sbt").value,
    verifyAlgorithm in verifyGenerate := HashAlgorithm.SHA1,
    verifyJars in verifyGenerate := verifyJarsTask(verifyGenerate).value,
    verifyOptions in verifyGenerate := VerifyOptions(),
    fullClasspath in verifyGenerate := (fullClasspath or (fullClasspath in Runtime)).value,
    externalDependencyClasspath in verifyGenerate := (externalDependencyClasspath or (externalDependencyClasspath in Runtime)).value
  )

  override def requires = sbt.plugins.JvmPlugin

  /*
   * Set the trigger to noTrigger so that the consumer can be explicit about enabling this plugin for the projects
   * that require it.
   * When there are multiple projects, some of them may not require verification of libraries. Leave it to the user
   * to determine where this plugin is needed.
   */
  override def trigger = noTrigger

  override lazy val projectSettings: Seq[Def.Setting[_]] = baseVerifySettings
}
