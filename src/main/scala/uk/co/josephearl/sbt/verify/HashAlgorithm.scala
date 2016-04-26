package uk.co.josephearl.sbt.verify

import java.io.File
import java.security.MessageDigest

import sbt.IO

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
