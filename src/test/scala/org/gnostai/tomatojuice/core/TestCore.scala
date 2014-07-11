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
import java.nio.file.Paths

trait TestCoreConfigurationModule extends CoreConfigurationModule {

    
  object TestConfig extends ConfigBuilder {
       
     lazy val userConfigFile: File = {
      val currDir = System.getProperty("user.dir")
      val confFilePath = Paths.get(currDir, "src", "test", "resources", "test.conf")
      confFilePath.toAbsolutePath().toFile()
    }
     
    println(userConfigFile.getAbsolutePath()) 
  }

  def config = TestConfig.config

}

trait TestCoreModule extends CoreModule  
  with TestCoreConfigurationModule
  with CoreDomainModule
  with CoreMessagesModule
  
object TestConfiguration extends TestCoreConfigurationModule {}


object Foo extends App {
  println(TestConfiguration.config.getInt("tomatojuice.pomodoro.duration")) 
}
