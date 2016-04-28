/*
 * Copyright (c) 2015-2016 Joseph Earl & contributors.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the under the Apache License,
 * Version 2.0 which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package uk.co.josephearl.sbt.verify

final case class RichModuleID private[sbt](m: sbt.ModuleID) {
  def sha1(hash: String): VerifyID = VerifyID(m, hash, HashAlgorithm.SHA1)
  def md5(hash: String): VerifyID = VerifyID(m, hash, HashAlgorithm.MD5)
}

object RichModuleID {
  implicit def toRichModuleID(m: sbt.ModuleID): RichModuleID = RichModuleID(m)
}
