lazy val root = (project in file("."))
    .enablePlugins(VerifyPlugin)
    .settings(
      name := "verify-unused-pass",
      organization := "uk.co.josephearl",
      version := "0.2.0",
      scalaVersion := "2.10.4",

      verifyDependencies in verify ++= Seq(
        "org.scala-lang" % "scala-library" % "2.10.4" SHA1 "9aae4cb1802537d604e03688cab744ff47b31a7d",
        "org.joda" % "joda-money" % "0.11" SHA1 "9aae4cb1802537d604e03688cab744ff47b31a7d"
      ),

      verifyOptions in verify := VerifyOptions(warnOnUnusedVerifications = true)
    )
