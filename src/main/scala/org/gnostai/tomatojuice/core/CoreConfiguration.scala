package org.gnostai.tomatojuice.core

import java.io.File
import com.typesafe.config.ConfigFactory

trait CoreConfigurationModule {

  object CoreConfig {

    lazy val userConfigFile: File = {
      val homeDir = System.getProperty("user.home")
      val configDir = new File(homeDir, ".config")
      val tjConfigDir = new File(configDir, "tomatojuice")
      new File(tjConfigDir, "application.conf")
    }

    lazy val userConfig = ConfigFactory.parseFile(userConfigFile)

    lazy val classpathConfig = ConfigFactory.load()

    lazy val config = {
      userConfig.withFallback(classpathConfig).resolve()
    }

  }

  def config = CoreConfig.config

}