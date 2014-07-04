package org.gnostai.tomatojuice.actors

import akka.actor._
import scala.concurrent.Future
import scala.concurrent.duration._
import org.gnostai.tomatojuice.core.CoreConfigurationModule

trait PomodoroCountdownModule extends CoreConfigurationModule {

  object PomodoroCountdown {
    case class StartCountdown(minutes: Int)
    case class MinutesRemaining(remaining: Int)
    case object TimerCompleted
    case object CancelTimer
    case object TimerCancelled
  }

  class PomodoroCountdownActor extends Actor with ActorLogging {

    import PomodoroCountdown._

    val pomodoroConfig = config.getConfig("tomatojuice.pomodoro")
    val pomodoroTracker = context.parent

    private val OneMinute = {
      /* 
       * Obviously, this is not intended for public use, and should eventually be removed
       */
      if (pomodoroConfig.getBoolean("dummyTestTimer")) {
        500 milliseconds
      } else {
        60 seconds
      }
    }

    def receive = timerInactive

    def timerInactive: Receive = {
      case StartCountdown(remaining) =>
        scheduleOneMinuteCallback(remaining)
      case x => log.warning("Unexpected timer message while timerInactive: " + x)
    }

    def timerActive(timer: Cancellable): Receive = {
      case MinutesRemaining(remaining) =>
        if (remaining > 0) {
          scheduleOneMinuteCallback(remaining)
        } else {
          pomodoroTracker ! TimerCompleted
          context.become(timerInactive)
        }
      case CancelTimer =>
        timer.cancel()
        context.parent ! TimerCancelled
      case x => log.info("  ACTIVE got " + x)
    }

    private def scheduleOneMinuteCallback(currentMinutesRemaining: Int) {
      pomodoroTracker ! MinutesRemaining(currentMinutesRemaining)
      implicit val dispatcher = context.dispatcher
      val next = currentMinutesRemaining - 1
      val cancellable = context.system.scheduler.scheduleOnce(OneMinute, self, MinutesRemaining(next))
      context.become(timerActive(cancellable))
    }

  }
}
