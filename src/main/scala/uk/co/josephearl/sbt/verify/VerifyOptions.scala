/*
 * Copyright (c) 2015-2016 Joseph Earl & contributors.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the under the Apache License,
 * Version 2.0 which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package uk.co.josephearl.sbt.verify

import sbt.Keys._

case class VerifyOptions (
  // include compiled jars from sub-projects
  includeBin: Boolean = true,
  // include scala libraries
  includeScala: Boolean = true,
  // include jars from external dependencies
  includeDependency: Boolean = true,
  // exclude specific jars
  excludedJars: Classpath = Nil,
  // warn instead of failing the build if there are unverified dependencies
  warnOnUnverifiedFiles: Boolean = false,
  // warn instead of failing the build if there are unused dependency verifications
  warnOnUnusedVerifications: Boolean = false
)
