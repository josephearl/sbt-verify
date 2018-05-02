/*
 * Copyright (c) 2015-2016 Joseph Earl & contributors.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the under the Apache License,
 * Version 2.0 which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package uk.co.josephearl.sbt.verify

import sbt.ModuleID
import sbt.librarymanagement.DependencyBuilders.OrganizationArtifactName
import uk.co.josephearl.sbt.verify.HashAlgorithm.HashAlgorithm

import scala.language.implicitConversions

final case class RichGroupArtifactID private[sbt](organization: String, name: String) {
  def sha1(hash: String): VerifyID = toVerifyID(hash, HashAlgorithm.SHA1)

  def md5(hash: String): VerifyID = toVerifyID(hash, HashAlgorithm.MD5)

  private def toVerifyID(hash: String, hashAlgorithm: HashAlgorithm): VerifyID =
    VerifyID(organization, name, hash, hashAlgorithm)
}

object RichGroupArtifactID {
  implicit def from(g: OrganizationArtifactName): RichGroupArtifactID = {
    val moduleID = g % "1.0.0"
    RichGroupArtifactID(moduleID.organization, moduleID.name)
  }

  implicit def from(m: ModuleID): RichGroupArtifactID = RichGroupArtifactID(m.organization, m.name)
}
