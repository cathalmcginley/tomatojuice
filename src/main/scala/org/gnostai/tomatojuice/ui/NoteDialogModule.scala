package org.gnostai.tomatojuice.ui

import scala.concurrent.Future
import akka.actor.ActorRef
import org.gnostai.tomatojuice.core.CoreDomainModule

trait NoteDialogModule extends UIFacadeModule with CoreDomainModule {

  object PomodoroNoteDialog {
    case object PopUpNoteDialog
    case class PopUpDialog(handle: ApplicationHandle)
    case object DialogClosing
  }
  
  abstract class PomodoroNoteDialogFacade extends UIFacade {
    
    def popUp()
    
    def displayProjects(projects: Iterable[Project])
    
  }
  
  type POMODORO_NOTE_DIALOG <: PomodoroNoteDialogFacade
  
  def createNoteDialog(mainUi: ActorRef, handle: ApplicationHandle): Future[POMODORO_NOTE_DIALOG]
  
}