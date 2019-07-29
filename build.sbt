lazy val root = (project in file("."))
  .enablePlugins(SbtPlugin)
  .settings(
    name := "sbt-verify",
    organization := "uk.co.josephearl",
    version := "0.4.0",
    scalaVersion := "2.12.8",
    scalacOptions ++= Seq("-unchecked", "-deprecation", "-language:_"),
    licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html")),
    publishMavenStyle := false,
    publishArtifact in Test := false
  )
