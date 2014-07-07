/*
 * Copyright (C) 2014 Cathal Mc Ginley
 *
 * This file is part of TomatoJuice, a Pomodoro timer-tracker for GNOME.
 *
 * TomatoJuice is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * TomatoJuice is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TomatoJuice; see the file COPYING. If not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301 USA.
*/

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