lazy val root = (project in file("."))
    .enablePlugins(VerifyPlugin)
    .settings(
      name := "verify-unmanaged",
      organization := "uk.co.josephearl",
      version := "0.2.0",
      scalaVersion := "2.10.4",

      verifyOptions in verifyGenerate := VerifyOptions(includeBin = false, includeScala = false, includeDependency = true)
    )
