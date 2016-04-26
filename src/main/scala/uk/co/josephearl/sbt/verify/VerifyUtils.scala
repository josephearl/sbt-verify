package uk.co.josephearl.sbt.verify

import java.io.File

private[verify] object VerifyUtils {
  def isScalaLibraryFile(file: File): Boolean =
    Vector("scala-actors",
      "scala-compiler",
      "scala-continuations",
      "scala-library",
      "scala-parser-combinators",
      "scala-reflect",
      "scala-swing",
      "scala-xml") exists { x =>
      file.getName startsWith x
    }
}
