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
import java.security.MessageDigest

import sbt.IO

import scala.language.implicitConversions

object HashAlgorithm extends Enumeration {

  protected case class Val(algorithm: String) extends super.Val {
    def fileContentHash(file: File): String = {
      val content = IO.readBytes(file)
      hex(hash(MessageDigest.getInstance(algorithm.toUpperCase), content))
    }

    private def hash[T](m: MessageDigest, b: Array[Byte]): Array[Byte] = {
      m.digest(b)
    }

    private def hex(b: Array[Byte]): String = {
      b.map("%02X" format _).mkString.toLowerCase
    }
  }

  implicit def valueToHashAlgorithmVal(v: Value): Val = v.asInstanceOf[Val]

  type HashAlgorithm = Val

  val SHA1 = Val("sha1")
  val MD5 = Val("md5")
}
