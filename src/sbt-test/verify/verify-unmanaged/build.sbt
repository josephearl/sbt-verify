name := "verify-unmanaged"

organization := "uk.co.josephearl"

version := "0.2.0"

scalaVersion := "2.10.4"

verifyDependencies in verify += "lib" % "guava" % "19.0" sha1 "6ce200f6b23222af3d8abb6b6459e6c44f4bb0e9"

verifyOptions in verify := VerifyOptions(includeBin = false, includeScala = false, includeDependency = true)
