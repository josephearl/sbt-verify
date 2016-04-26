package uk.co.josephearl.sbt.verify

import uk.co.josephearl.sbt.verify.HashAlgorithm.HashAlgorithm
import sbt._

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
