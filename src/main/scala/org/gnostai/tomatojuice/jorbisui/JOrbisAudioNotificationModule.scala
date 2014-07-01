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

