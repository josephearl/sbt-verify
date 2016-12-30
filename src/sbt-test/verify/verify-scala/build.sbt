name := "verify-scala"

organization := "uk.co.josephearl"

version := "0.2.0"

scalaVersion := "2.10.4"

verifyDependencies in verify += "org.scala-lang" % "scala-library" sha1 "9aae4cb1802537d604e03688cab744ff47b31a7d"

verifyOptions in verify := VerifyOptions(includeBin = false, includeScala = true, includeDependency = false)
