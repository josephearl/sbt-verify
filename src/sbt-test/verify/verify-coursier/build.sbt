lazy val root = (project in file("."))
    .enablePlugins(VerifyPlugin)
    .settings(
      name := "verify-coursier",
      organization := "uk.co.josephearl",
      version := "0.4.0",
      scalaVersion := "2.10.4",

      libraryDependencies += "com.google.guava" % "guava" % "19.0"
    )
