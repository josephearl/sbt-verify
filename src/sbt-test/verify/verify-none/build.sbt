name := "verify-none"

organization := "uk.co.josephearl"

version := "0.2.0"

scalaVersion := "2.10.4"

verifyOptions in verify := VerifyOptions(includeBin = false, includeScala = false, includeDependency = false)
