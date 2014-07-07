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

package org.gnostai.tomatojuice.jorbisui

import org.gnostai.tomatojuice.ui.AudioNotificationModule
import org.newdawn.easyogg.OggClip
import scala.concurrent.Future

trait JOrbisAudioNotificationModule extends AudioNotificationModule {

  class JOrbisAudioNotificationFacade extends AudioNotificationFacade {
    
    private val initialPomodoroSound = new OggClip("sounds/scanning.ogg")
    private val pomodoroCompletedSound = new OggClip("sounds/good_scan.ogg")
    private val breakCompletedSound = new OggClip("sounds/bad_scan.ogg")
    
    
    def playInitialPomodoroSound() {      
      initialPomodoroSound.play()
    }
    
    def playPomodoroCompletedSound() {
      pomodoroCompletedSound.play()
    }
    
    def playBreakCompletedSound() {
      breakCompletedSound.play()
    }
    
  }
  
  type AUDIO_NOTIFICATION = JOrbisAudioNotificationFacade
  
  def createAudioNotification(): Future[AUDIO_NOTIFICATION] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    Future {
      new JOrbisAudioNotificationFacade()
    }
  }
}

