package org.gnostai.tomatojuice.ui.actors

import akka.actor._
import org.gnostai.tomatojuice.ui.NoteDialogModule
import akka.event.LoggingReceive
import org.gnostai.tomatojuice.core.CoreMessagesModule
import org.gnostai.tomatojuice.actors.PomodoroTrackerModule

trait PomodoroNoteDialogActorModule extends NoteDialogModule with CoreMessagesModule
  with PomodoroTrackerModule {

  object PomodoroNoteDialogActor {
    case class DisplayProjects(facade: POMODORO_NOTE_DIALOG)
    case object PopUp
    case class Save(project: Project, note: String)
  }

  class PomodoroNoteDialogActor(mainApp: ActorRef) extends Actor with ActorLogging {

    def receive = dialogNotVisible

    import PomodoroNoteDialogActor._
    import CoreMessages._
    import PomodoroNoteDialog._

    mainApp ! RegisterPomodoroListener(self)

    def dialogNotVisible: Receive = LoggingReceive {
      case PomodoroTracker.CountdownTimerCompleted(nextTimer) =>
        nextTimer match {

          case PomodoroCountdownTimer =>
          case ShortBreakCountdownTimer => context.parent ! PopUpNoteDialog
          case LongBreakCountdownTimer => context.parent ! PopUpNoteDialog
        }

      case PopUpDialog(handle) =>
        implicit val dispatcher = context.system.dispatcher
        val noteFacadeFuture = createNoteDialog(self, handle)
        context.become(dialogPending(handle))
        noteFacadeFuture onSuccess {
          case facade =>
            self ! DisplayProjects(facade)
            //facade.displayProjects(Seq(Project(Some(1), "No Project", "", None)))
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
      case Save(project: Project, note: String) =>
        handle.mainApp ! ConfirmPomodoroCompletedWithNote(project, note)
        context.parent ! NoteSaved
        facade.popDown() // TODO put this in a Future
        context.become(dialogNotVisible)
      case PopDownDialog(handle) =>
        facade.popDown()  // TODO put this in a Future
        context.become(dialogNotVisible)
        
        


      case DialogClosing =>
        context.become(dialogNotVisible)
      case x =>
        log.info("dialog visible: " + x)
    }
  }

}