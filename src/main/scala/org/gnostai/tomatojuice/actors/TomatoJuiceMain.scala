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
        
    val pomodoroTracker = context.actorOf(Props(new PomodoroTrackerActor(self)), "PomodoroTracker")
    val db = createDBMainActor(context, "TomatoJuiceDB")
    val uiMain = context.actorOf(Props(new TomatoJuiceUIMain(self)), "TomatoJuiceUI")
    
    override def receive = beforeStart orElse pomodoroListener 
    
    def beforeStart: Receive = LoggingReceive {
      case StartUI =>
        uiMain ! StartUp
        context.become(pomodoroInactive orElse pomodoroListener)
    }
    
    def pomodoroListener: Receive = LoggingReceive {
      case RegisterPomodoroListener(listener) =>
        pomodoroTracker ! Listen(listener)
    }
    
    def pomodoroInactive: Receive = LoggingReceive {
      case StartTimer =>        
        pomodoroTracker ! PomodoroTracker.TimerActivated        
      case NewPomodoroStarted =>
        db ! RecordPomodoroStart
      case PomodoroPersist.PomodoroCreated(id) => 
        context.become(pomodoroActive(id) orElse pomodoroListener)
    }
    
    def pomodoroActive(id: POMODORO_ID): Receive = LoggingReceive{
      case ConfirmPomodoroCompleted =>
        db ! RecordPomodoroCompleted(id)
        context.become(pomodoroInactive orElse pomodoroListener)
    }
  }

}