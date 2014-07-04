package org.gnostai.tomatojuice.actors

import akka.actor._
import akka.event.LoggingReceive
import org.gnostai.tomatojuice.ui.actors.TomatoJuiceUIMainModule
import org.gnostai.tomatojuice.persist.PersistModule
import org.gnostai.tomatojuice.core.CoreMessagesModule
import akka.routing.Listen

trait TomatoJuiceMainModule extends CoreMessagesModule with PomodoroTrackerModule {

  this: TomatoJuiceUIMainModule with PersistModule =>
  
    
  object TomatoJuiceMain {
    case object StartUI
    
  }
    
    
  class TomatoJuiceMainActor extends Actor with ActorLogging {

    import CoreMessages._
    import TomatoJuiceMain._
    import PomodoroTracker._
    
    val uiMain = context.actorOf(Props(new TomatoJuiceUIMain(self)), "TomatoJuiceUI")
    val pomodoroTracker = context.actorOf(Props(new PomodoroTrackerActor(self)), "PomodoroTracker")
    val db = createDBMainActor(context, "TomatoJuiceDB")
    
    override def receive: Receive = LoggingReceive {
      case StartUI =>
        uiMain ! StartUp
        context.become(pomodoroInactive)
      case RegisterPomodoroListener(listener) =>
        pomodoroTracker ! Listen(listener)
      case x => log.info(" ???? main " + x)
    }
    
    def pomodoroInactive: Receive = LoggingReceive {
      case StartTimer =>        
        pomodoroTracker ! TimerActivated        
      case NewPomodoroStarted =>
        db ! RecordPomodoroStart
      case PomodoroPersist.PomodoroCreated(id) => 
        context.become(pomodoroActive(id))
      case RegisterPomodoroListener(listener) =>  // copied
        pomodoroTracker ! Listen(listener)        // copied
      case x => log.info(" !!!!! main " + x)
    }
    
    def pomodoroActive(id: POMODORO_ID): Receive = LoggingReceive{
      case ConfirmPomodoroCompleted =>
        db ! RecordPomodoroCompleted(id)
        context.become(pomodoroInactive)
      case x =>
        log.info(" !`!`!`!`! main active " + x)
    }
  }

}