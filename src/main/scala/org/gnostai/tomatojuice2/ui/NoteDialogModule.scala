package org.gnostai.tomatojuice2.ui

import scala.concurrent.Future

trait NoteDialogModule extends UIFacadeModule {

  abstract class PomodoroNodeDialogFacade extends UIFacade {
    
    def popUp()
    
  }
  
  type POMODORO_NOTE_DIALOG <: PomodoroNodeDialogFacade
  
  def createNoteDialog(handle: ApplicationHandle): Future[PomodoroNodeDialogFacade]
  
}