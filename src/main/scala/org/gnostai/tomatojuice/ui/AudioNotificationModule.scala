package org.gnostai.tomatojuice.ui

import scala.concurrent.Future

trait AudioNotificationModule {

  abstract class AudioNotificationFacade {
    
    def playInitialPomodoroSound()
    def playPomodoroCompletedSound()
    def playBreakCompletedSound()
    
  }
  
  type AUDIO_NOTIFICATION <: AudioNotificationFacade
  
  def createAudioNotification(): Future[AUDIO_NOTIFICATION]
}

