package org.gnostai.tomatojuice.actors

import akka.actor._
import akka.event.LoggingReceive
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
    
    override def receive: Receive = LoggingReceive {
      case StartUI =>
        uiMain ! StartUp
        context.become(pomodoroInactive)
        
      case x => log.info(" ???? main " + x)
    }
    
    def pomodoroInactive: Receive = LoggingReceive {
      case "StartPomodoro" =>
        println("     .................... start pomodoro")
        db ! RecordPomodoroStart          
      case PomodoroCreated(id) => 
        println("     .................... pomodoro created")
        context.become(pomodoroActive(id))
      case x => log.info(" !!!!! main " + x)
    }
    
    def pomodoroActive(id: POMODORO_ID): Receive = LoggingReceive{
      case "CompletedPomodoro" =>
        db ! RecordPomodoroCompleted(id)
        context.become(pomodoroInactive)
      case x =>
        log.info(" !`!`!`!`! main active " + x)
    }
  }

}