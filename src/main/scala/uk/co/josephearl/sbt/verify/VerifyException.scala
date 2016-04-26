package uk.co.josephearl.sbt.verify

case class VerifyException(message: String, cause: Throwable = null) extends RuntimeException(message, cause)
