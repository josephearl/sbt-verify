/*
 * Copyright (c) 2015-2016 Joseph Earl & contributors.
 * All rights reserved.
 *
 * This program and the accompanying materials are made available under the terms of the under the Apache License,
 * Version 2.0 which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 */
package uk.co.josephearl.sbt.verify

import sbt.Logger

class VerifyLogger(logger: Logger, plugin: String = "sbt-verify", project: String = "default") {
  def mkMessage(message: String) = s"[$plugin] [$project] $message"

  def debug(message: String): Unit = logger.debug(mkMessage(message))

  def info(message: String): Unit = logger.info(mkMessage(message))

  def warn(message: String): Unit = logger.warn(mkMessage(message))

  def error(message: String): Unit = logger.error(mkMessage(message))
}
