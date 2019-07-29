sys
  .props
  .get("plugin.version")
  .map(pluginVersion => addSbtPlugin("uk.co.josephearl" % "sbt-verify" % pluginVersion))
  .getOrElse(sys.error("""|The system property 'plugin.version' is not defined.
                          |Specify this property using the scriptedLaunchOpts -D.""".stripMargin))
