package org.gnostai.tomatojuice.ui

import scala.concurrent.Future

trait NoteDialogModule extends UIFacadeModule {

  abstract class PomodoroNodeDialogFacade extends UIFacade {
    
    def popUp()
    
  }
  
  type POMODORO_NOTE_DIALOG <: PomodoroNodeDialogFacade
  
  def createNoteDialog(handle: ApplicationHandle): Future[POMODORO_NOTE_DIALOG]
  
}