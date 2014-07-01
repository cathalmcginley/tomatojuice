package org.gnostai.tomatojuice.actors

import akka.actor._

import org.gnostai.tomatojuice.ui.actors.TomatoJuiceUIMainModule
import org.gnostai.tomatojuice.persist.PersistModule

trait TomatoJuiceMainModule {

  this: TomatoJuiceUIMainModule with PersistModule =>
  
    
    
    
    
  object TomatoJuiceMain {
    case object StartUI
  }
    
    
  class TomatoJuiceMainActor extends Actor with ActorLogging {

    import TomatoJuiceMain._
    
    val uiMain = context.actorOf(Props(new TomatoJuiceUIMain()), "TomatoJuiceUI")
    val db = createDBMainActor(context, "TomatoJuiceDB")
    
    override def receive: Receive = {
      case StartUI =>
        uiMain ! StartUp
      case "StartPomodoro" =>
        db ! RecordPomodoroStart
      case x => log.info("main " + x)
    }
  }

}