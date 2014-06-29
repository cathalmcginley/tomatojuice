package org.gnostai.tomatojuice2.ui.actors

import akka.actor._
import scala.concurrent.Future


trait TomatoJuiceMainModule extends StatusIconActorModule {

  case object StartUp

  class TomatoJuiceMain extends Actor with ActorLogging {

    val statusIcon = context.actorOf(Props(new StatusIconActor), "TomatoJuice")
    
    def receive: Receive = {
      case StartUp =>
        log.info("StartUp:: aaa")
        import context.dispatcher
        Future {
          initializeGui(context.system, self)
        } onSuccess {
          case handle =>
            log.info("got handle" + handle)
            context.become(activeGuiRunning(handle))
            //statusIcon ! DisplayInitialStatusIcon(handle)
        } 
        
      case x => log.info("main: " + x)
    }

    def activeGuiRunning(guiHandle: GUI_HANDLE): Receive = {
      case GuiActivated(handle) =>
        log.info("activeGuiRunning got GuiActivated " + handle)
        statusIcon ! DisplayInitialStatusIcon(handle)
      case x => log.info("?active " + x)
    }
    
  }

}