package org.gnostai.tomatojuice2.ui.actors

import akka.actor._
import org.gnostai.tomatojuice2.ui.StatusIconModule
import scala.concurrent.Future

trait StatusIconActorModule extends StatusIconModule {

  class StatusIconActor extends Actor {

    def receive: Receive = {
      case DisplayInitialStatusIcon(handle) =>
        println("StatusIconActor got DisplayInitialStatusIcon")
        import context.dispatcher
        val iconFuture = constructStatusIcon(self, handle) 
        iconFuture onSuccess {
          case iconFacade => println("! got icon facade " + iconFacade) 
        }
      case StatusIconActivated => println("activated")
    }
    
  }

}