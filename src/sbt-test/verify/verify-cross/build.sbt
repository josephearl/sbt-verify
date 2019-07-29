lazy val root = (project in file("."))
    .enablePlugins(VerifyPlugin)
    .settings(
      name := "verify-cross",
      organization := "uk.co.josephearl",
      version := "0.2.0",
      crossScalaVersions := Seq("2.10.4", "2.12.8"),
      verifyDependencies in verify ++= Seq(
        scalaVersion.value match {
          case "2.10.4" => "org.scala-lang" % "scala-library" % "2.10.4" SHA1 "9aae4cb1802537d604e03688cab744ff47b31a7d"
          case "2.12.8" => "org.scala-lang" % "scala-library" % "2.12.8" SHA1 "36b234834d8f842cdde963c8591efae6cf413e3f"
          // Define a default as SBT may initially boot up with any version of Scala. However, this won't be used in the test.
          case _ => "org.scala-lang" % "scala-library" SHA1 "we-should-never-get-here"
        }
      )
    )
