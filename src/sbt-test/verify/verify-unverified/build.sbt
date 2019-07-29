lazy val root = (project in file("."))
    .enablePlugins(VerifyPlugin)
    .settings(
      name := "verify-unverified",
      organization := "uk.co.josephearl",
      version := "0.2.0",
      scalaVersion := "2.10.4",
    )
