package org.gnostai.tomatojuice.ui.actors

import akka.actor._
import scala.concurrent.Future
import akka.event.LoggingReceive
import org.gnostai.tomatojuice.ui.NoteDialogModule
import org.gnostai.tomatojuice.ui.StatusIconModule


trait TomatoJuiceUIMainModule { 
  this: StatusIconActorModule with StatusIconModule with NoteDialogModule =>

  case object StartUp
  

  class TomatoJuiceUIMain(mainApp: ActorRef) extends Actor with ActorLogging {

    val statusIcon = context.actorOf(Props(new StatusIconActor(mainApp)), "PomodoroCountdownIcon")
    
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
        implicit val dispatcher = context.system.dispatcher
        val noteFacadeFuture = createNoteDialog(self, appHandle)
        noteFacadeFuture onSuccess {
          case facade => facade.popUp()
        }
      case x => 
        log.warning("got unexpected message " + x + "; sending to mainApp")
        mainApp ! x
    }
    
  }

}