package uk.co.josephearl.sbt.verify

final case class RichModuleID private[sbt](m: sbt.ModuleID) {
  def sha1(hash: String): VerifyID = VerifyID(m, hash, HashAlgorithm.SHA1)
  def md5(hash: String): VerifyID = VerifyID(m, hash, HashAlgorithm.MD5)
}

object RichModuleID {
  implicit def toRichModuleID(m: sbt.ModuleID): RichModuleID = RichModuleID(m)
}
