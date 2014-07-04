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
      if (pomodoroConfig.getBoolean("dummyTestTimer")) {
        500 milliseconds
      } else {
        pomodoroConfig.getInt("secondsPerMinute") seconds
      }
    }

    def receive = timerInactive

    def timerInactive: Receive = {
      case StartCountdown(remaining) =>
        log.info("starting with timer at " + remaining)
        scheduleOneMinuteCallback(remaining)

      case x => log.info("inactive got " + x)
    }

    def timerActive(timer: Cancellable): Receive = {
      case MinutesRemaining(remaining) =>
        log.info(" ... remaining " + remaining)
        if (remaining > 0) {
          scheduleOneMinuteCallback(remaining)
        } else {
          log.info("timer done!!")
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
      implicit val executionContext = context.dispatcher
      val next = currentMinutesRemaining - 1
      val cancellable = context.system.scheduler.scheduleOnce(OneMinute, self, MinutesRemaining(next))
      context.become(timerActive(cancellable))
    }

  }

  //  def createCountdownActor(implicit context: ActorContext) = {
  //    context.actorOf(Props(new PomodoroCountdownActor))
  //  }
}