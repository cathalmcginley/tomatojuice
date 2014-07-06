package org.gnostai.tomatojuice.ui.actors

import akka.actor._
import org.gnostai.tomatojuice.ui.NoteDialogModule
import akka.event.LoggingReceive
import org.gnostai.tomatojuice.core.CoreMessagesModule


trait PomodoroNoteDialogActorModule extends NoteDialogModule with CoreMessagesModule {

  object PomodoroNoteDialogActor {
    case class DisplayProjects(facade: POMODORO_NOTE_DIALOG)
    case object PopUp
    
  }


  class PomodoroNoteDialogActor extends Actor with ActorLogging {

    def receive = dialogNotVisible

    import PomodoroNoteDialogActor._
    import PomodoroNoteDialog._

    def dialogNotVisible: Receive = LoggingReceive {
      case PopUpDialog(handle) =>
        implicit val dispatcher = context.system.dispatcher
        val noteFacadeFuture = createNoteDialog(self, handle)
        context.become(dialogPending(handle))
        noteFacadeFuture onSuccess {
          case facade => 
            self ! DisplayProjects(facade)
            facade.displayProjects(Seq(Project(Some(1), "No Project", "", None)))
            facade.popUp()
            
        }
    }
    
    def dialogPending(handle: ApplicationHandle): Receive = LoggingReceive {
      case DisplayProjects(facade) =>
        // TODO send message to main, asking for list of projects
        println(">>>>>>>>> " + handle.mainApp.path)
        handle.mainApp ! CoreMessages.GetProjectList
        context.become(dialogVisible(handle, facade))
      case x => 
        log.info("dialog visible: " + x)
    }
    
    def dialogVisible(handle: ApplicationHandle, facade: POMODORO_NOTE_DIALOG): Receive = LoggingReceive {
      case CoreMessages.ProjectList(projects) =>
        facade.displayProjects(projects)
      case PopUp =>
        facade.popUp()
      case DialogClosing =>
        context.become(dialogNotVisible)
      case x => 
        log.info("dialog visible: " + x)
    }
  }

}