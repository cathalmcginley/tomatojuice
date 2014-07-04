package org.gnostai.tomatojuice.ui.actors

import akka.actor._
import org.gnostai.tomatojuice.ui.StatusIconModule
import scala.concurrent.Future
import org.gnostai.tomatojuice.ui.AudioNotificationModule
import org.gnostai.tomatojuice.core.CoreMessagesModule

trait StatusIconActorModule extends PomodoroCountdownActorModule 
with StatusIconModule 
with AudioNotificationModule 
with CoreMessagesModule {
  
  //case class DisplayMinutesRemaining(remaining: Int, timer: CountdownType) extends Message
  //case class CountdownFinished(timer: CountdownType) extends Message
  
  class StatusIconActor extends Actor with ActorLogging {

    import CoreMessages._
    
    val countdownActor = context.actorOf(Props(new PomodoroCountdownActor))
    
    
    val audio = createAudioNotification()
    
    def receive = notInitialized
    
    private def minutesToCountDown(countdown: CountdownType) = {
      countdown match {
        case PomodoroCountdown => 2
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
        
        val mainApp = context.actorSelection("../..")
        log.info(" >>> " + mainApp)
        mainApp ! StartNewPomodoro
        
        implicit val disp = context.system.dispatcher
        for (facade <- audio) { facade.playInitialPomodoroSound() }
    } 
    
    def countingDown(iconFacade: STATUS_ICON, countdown: CountdownType): Receive = {
      case MinutesRemaining(mins) =>
        iconFacade.showMinutesRemaining(mins, countdown)
      case TimerCompleted =>
        iconFacade.showMinutesRemaining(0, countdown)
        iconFacade.timerCompleted()                
        
        
        val mainApp = context.actorSelection("../..")
        log.info(" >>> " + mainApp)
        mainApp ! ConfirmPomodorCompleted
        context.become(timerInactive(iconFacade, nextCountdownFor(countdown)))
        implicit val disp = context.system.dispatcher
        for (facade <- audio) { facade.playPomodoroCompletedSound() }
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