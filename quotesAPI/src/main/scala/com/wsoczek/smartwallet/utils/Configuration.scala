package com.wsoczek.smartwallet.utils

import com.typesafe.config.{Config, ConfigFactory}

trait Configuration {
  val config: Config = ConfigFactory.load("application.conf")

  def appProperties: Config = config.getConfig("app")
}
