package org.gnostai.tomatojuice.ui.actors

import akka.actor._
import scala.concurrent.Future


trait TomatoJuiceUIMainModule extends StatusIconActorModule {

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

    def activeGuiRunning(guiHandle: GUI_HANDLE): Receive = {
      case GuiActivated(handle) =>
        statusIcon ! DisplayInitialStatusIcon(handle)
      case x => 
        log.warning("got unexpected message " + x + "; sending to mainApp")
        mainApp ! x
    }
    
  }

}