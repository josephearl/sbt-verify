/*
 * Copyright (c) 2015-2016 Joseph Earl & contributors.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the under the Apache License,
 * Version 2.0 which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 */
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
