package org.gnostai.tomatojuice.ui.actors

import akka.actor._
import scala.concurrent.Future
import akka.event.LoggingReceive
import org.gnostai.tomatojuice.ui.NoteDialogModule
import org.gnostai.tomatojuice.ui.StatusIconModule


trait TomatoJuiceUIMainModule { 
  this: StatusIconActorModule with StatusIconModule
  with PomodoroNoteDialogActorModule with NoteDialogModule =>

  case object StartUp
  

  class TomatoJuiceUIMain(mainApp: ActorRef) extends Actor with ActorLogging {

    val statusIcon = context.actorOf(Props(new StatusIconActor(mainApp)), "PomodoroCountdownIcon")
    val noteDialog = context.actorOf(Props(new PomodoroNoteDialogActor()), "PomodoroNoteDialog")
    
    def receive: Receive = {
      case StartUp =>
        import context.dispatcher
        Future {
          initializeGui(context.system, self)
        } onSuccess {
          case handle =>
            context.become(activeGuiRunning(handle))
        } 
        
      case x => log.info("main: " + x)
    }

    def activeGuiRunning(guiHandle: GUI_HANDLE): Receive = LoggingReceive {
      case GuiActivated(handle) =>
        statusIcon ! DisplayInitialStatusIcon(handle)
      case "PopUpNoteDialog" =>
        val appHandle = ApplicationHandle(context.system, self, guiHandle)
        noteDialog ! PomodoroNoteDialog.PopUpDialog(appHandle)
        
      case x => 
        log.warning("got unexpected message " + x + "; sending to mainApp")
        mainApp ! x
    }
    
  }

}