package org.gnostai.tomatojuice.actors

import akka.actor._
import scala.concurrent.Future
import org.gnostai.tomatojuice.core.CoreMessagesModule
import org.gnostai.tomatojuice.core.CoreConfigurationModule
import akka.routing.Listeners
import org.gnostai.tomatojuice.core.CoreDomainModule

trait PomodoroTrackerModule extends PomodoroCountdownModule
  with CoreDomainModule
  with CoreMessagesModule
  with CoreConfigurationModule {

  object PomodoroTracker {
    case object TimerActivated
    case class CountdownMinutesRemaining(timer: CountdownTimer, minutes: Int)
    case class CountdownTimerCompleted(timer: CountdownTimer)

  }

  //case class DisplayMinutesRemaining(remaining: Int, timer: CountdownType) extends Message
  //case class CountdownFinished(timer: CountdownType) extends Message

  class PomodoroTrackerActor(mainApp: ActorRef) extends Actor
    with Listeners
    with ActorLogging {

    import CoreMessages._
    import PomodoroTracker._
    import PomodoroCountdown._

    val pomodoroConfig = config.getConfig("tomatojuice.pomodoro")
    val countdownActor = context.actorOf(Props(new PomodoroCountdownActor))

    def receive = timerInactive(PomodoroCountdownTimer, pomodorosBeforeLongBreak) orElse listenerManagement

    def timerInactive(nextCountdown: CountdownTimer, pomodorosRemaining: Int): Receive = {
      case TimerActivated =>
        log.info("NEW NEW activated - TODO start " + nextCountdown + " timer or whatever")
        context.become(countingDown(nextCountdown, pomodorosRemaining) orElse listenerManagement)
        val minutes = minutesToCountDown(nextCountdown)
        countdownActor ! StartCountdown(minutes)
        mainApp ! NewPomodoroStarted

    }

    def countingDown(timer: CountdownTimer, pomodorosRemaining: Int): Receive = {
      case MinutesRemaining(mins) =>
        log.info("NEW NEW  mins remaining " + mins)
        gossip(CountdownMinutesRemaining(timer, mins))
      //iconFacade.showMinutesRemaining(mins, countdown)
      case TimerCompleted =>
        log.info("NEW NEW  timer completed")
        gossip(CountdownMinutesRemaining(timer, 0))
        
        //        iconFacade.showMinutesRemaining(0, countdown)
        //        iconFacade.timerCompleted()

      val remaining = timer match {
          case PomodoroCountdownTimer => pomodorosRemaining
          case ShortBreakCountdownTimer => pomodorosRemaining - 1
          case LongBreakCountdownTimer => pomodorosBeforeLongBreak
        }
        
        //val remainingAfterCompletion = pomodorosRemaining - 1
        val nextTimer = nextTimerFor(timer, remaining)
        
        gossip(CountdownTimerCompleted(nextTimer))
        
         
        context.become(timerInactive(nextTimer, remaining))
      //        implicit val disp = context.system.dispatcher
      //        for (facade <- audio) { facade.playPomodoroCompletedSound() }
    }

    private def pomodorosBeforeLongBreak = pomodoroConfig.getInt("pomodorosBeforeLongBreak")

    private def nextTimerFor(countdown: CountdownTimer, pomodorosRemaining: Int): CountdownTimer = {
      countdown match {
        case PomodoroCountdownTimer => 
          if (pomodorosRemaining > 1)
            ShortBreakCountdownTimer
          else
            LongBreakCountdownTimer
        case ShortBreakCountdownTimer => PomodoroCountdownTimer          
        case LongBreakCountdownTimer => PomodoroCountdownTimer
      }
    }

    private def minutesToCountDown(countdown: CountdownTimer) = {
      countdown match {
        case PomodoroCountdownTimer => pomodoroConfig.getInt("duration")
        case ShortBreakCountdownTimer => pomodoroConfig.getInt("breakDuration")
        case LongBreakCountdownTimer => pomodoroConfig.getInt("longBreakDuration")
      }
    }

  }

}