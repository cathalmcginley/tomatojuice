package org.gnostai.tomatojuice.ui

import scala.concurrent.Future
import akka.actor.ActorRef

trait NoteDialogModule extends UIFacadeModule {

  object PomodoroNoteDialog {
    case class PopUpDialog(handle: ApplicationHandle)
  }
  
  abstract class PomodoroNoteDialogFacade extends UIFacade {
    
    def popUp()
    
  }
  
  type POMODORO_NOTE_DIALOG <: PomodoroNoteDialogFacade
  
  def createNoteDialog(mainUi: ActorRef, handle: ApplicationHandle): Future[POMODORO_NOTE_DIALOG]
  
}