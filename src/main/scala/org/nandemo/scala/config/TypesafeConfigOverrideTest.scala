package org.nandemo.scala.config

import com.typesafe.config.ConfigFactory

object TypesafeConfigOverrideTest extends App {

  // check typesafe config override
  val config = ConfigFactory.load()

  //-Dtypesafe.config.override=jvmoption
  println(config.getString("typesafe.config.override"))

  println(config.getConfig("typesafe"))
}
