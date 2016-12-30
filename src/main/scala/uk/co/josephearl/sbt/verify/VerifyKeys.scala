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
  // tasks
  lazy val verify = TaskKey[Unit]("verify")
  lazy val verifyGenerate = TaskKey[File]("verifyGenerate")

  // common keys
  lazy val verifyOptions = TaskKey[VerifyOptions]("verify-options")
  lazy val verifyJars = TaskKey[Seq[File]]("verify-jars")

  // verify task settings
  lazy val verifyDependencies = TaskKey[Seq[VerifyID]]("verify-dependencies")

  // verifyGenerate task settings
  lazy val verifyOutputFile = TaskKey[File]("verifyGenerate-output-file")
  lazy val verifyAlgorithm = TaskKey[HashAlgorithm]("verifyGenerate-algorithm")
}
