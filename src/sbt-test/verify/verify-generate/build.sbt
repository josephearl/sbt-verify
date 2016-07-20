logLevel := Level.Debug

name := "verify-scala"

organization := "uk.co.josephearl"

version := "0.2.0"

scalaVersion := "2.10.4"

libraryDependencies ++= Seq(
  "org.json4s" %% "json4s-core" % "3.2.11",
  "org.joda" % "joda-money" % "0.11"
)

verifyOutputFile in verifyGenerate := baseDirectory.value / "verify.sbt"
