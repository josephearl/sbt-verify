sbtPlugin := true

name := "sbt-verify"

organization := "uk.co.josephearl"

version := "0.3.0"

scalaVersion := "2.12.5"

scalacOptions ++= Seq("-unchecked", "-deprecation", "-language:_")

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

publishMavenStyle := false

publishArtifact in Test := false
