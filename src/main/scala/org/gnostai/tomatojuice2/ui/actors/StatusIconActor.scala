package org.gnostai.tomatojuice2.ui.actors

import akka.actor._
import org.gnostai.tomatojuice2.ui.StatusIconModule
import scala.concurrent.Future

trait StatusIconActorModule extends PomodoroCountdownActorModule with StatusIconModule  {
  
  //case class DisplayMinutesRemaining(remaining: Int, timer: CountdownType) extends Message
  //case class CountdownFinished(timer: CountdownType) extends Message
  
  class StatusIconActor extends Actor with ActorLogging {

    val countdownActor = context.actorOf(Props(new PomodoroCountdownActor)) 
    
    def receive = notInitialized
    
    private def minutesToCountDown(countdown: CountdownType) = {
      countdown match {
        case PomodoroCountdown => 25
        case ShortBreakCountdown => 5
        case LongBreakCountdown => 25
      }
    }
    
    def notInitialized: Receive = {
      case DisplayInitialStatusIcon(handle) =>
        log.info("StatusIconActor got DisplayInitialStatusIcon")
        import context.dispatcher
        val iconFuture = constructStatusIcon(self, handle) 
        iconFuture onSuccess {
          case iconFacade => println("! got icon facade " + iconFacade)
          context.become(timerInactive(iconFacade, PomodoroCountdown))
        }
    }   
    
    def timerInactive(iconFacade: STATUS_ICON, nextCountdown: CountdownType): Receive = {
      case StatusIconActivated => 
        log.info("activated - TODO start " + nextCountdown + " timer or whatever")
        context.become(countingDown(iconFacade, nextCountdown))
        countdownActor ! StartCountdown(minutesToCountDown(nextCountdown))
    } 
    
    def countingDown(iconFacade: STATUS_ICON, countdown: CountdownType): Receive = {
      case MinutesRemaining(mins) =>
        iconFacade.showMinutesRemaining(mins, countdown)
      case TimerCompleted =>
        iconFacade.showMinutesRemaining(0, countdown)
        iconFacade.timerCompleted()
        context.become(timerInactive(iconFacade, nextCountdownFor(countdown)))
        
    }
    
    private def nextCountdownFor(countdown: CountdownType) = {
      countdown match {
        case PomodoroCountdown => ShortBreakCountdown  // TODO intermittent longer breaks
        case ShortBreakCountdown => PomodoroCountdown
        case LongBreakCountdown => PomodoroCountdown
      }
     
    }
    
  }

}