package org.nandemo.scala.config

import com.typesafe.config.{ConfigFactory, ConfigRenderOptions}

object TypesafeConfigOverrideTest extends App {

  // check typesafe config override
  val config = ConfigFactory.load()

  //-Dtypesafe.config.override=jvmoption
  println(config.getString("typesafe.config.override"))

  val prettyPrintConfigs: String = config.getConfig("typesafe")
    .root()
    .render(ConfigRenderOptions.concise())

  println(prettyPrintConfigs)
}
