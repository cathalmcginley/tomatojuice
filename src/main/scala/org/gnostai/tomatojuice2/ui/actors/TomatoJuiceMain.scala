package org.gnostai.tomatojuice2.ui.actors

import akka.actor._
import scala.concurrent.Future


trait TomatoJuiceMainModule extends StatusIconActorModule {

  case object StartUp

  class TomatoJuiceMain extends Actor {

    val statusIcon = context.actorOf(Props(new StatusIconActor))
    
    def receive: Receive = {
      case StartUp =>
        println("StartUp:: aaa")
        import context.dispatcher
        Future {
          initializeGui(context.system, self)
        } onSuccess {
          case handle =>
            println("got handle" + handle)
            context.become(activeGuiRunning(handle))
            //statusIcon ! DisplayInitialStatusIcon(handle)
        } 
        
      case x => println("main: " + x)
    }

    def activeGuiRunning(guiHandle: GUI_HANDLE): Receive = {
      case GuiActivated(handle) =>
        println("activeGuiRunning got GuiActivated " + handle)
        statusIcon ! DisplayInitialStatusIcon(handle)
      case x => println("?active " + x)
    }
    
  }

}