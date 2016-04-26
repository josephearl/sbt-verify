name := "verify-cross"

organization := "uk.co.josephearl"

version := "0.2.0"

crossScalaVersions := Seq("2.10.4", "2.10.5")

verifyDependencies in verify ++= Seq(
  scalaVersion.value match {
    case "2.10.4" => "org.scala-lang" % "scala-library" % "2.10.4" sha1 "9aae4cb1802537d604e03688cab744ff47b31a7d"
    case "2.10.5" => "org.scala-lang" % "scala-library" % "2.10.5" sha1 "57ac67a6cf6fd591e235c62f8893438e8d10431d"
  }
)
