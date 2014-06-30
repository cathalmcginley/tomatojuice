package org.gnostai.tomatojuice2.ui.actors

import akka.actor._
import org.gnostai.tomatojuice2.ui.StatusIconModule
import scala.concurrent.Future
import scala.concurrent.duration._
import org.gnostai.tomatojuice2.ui.UIFacadeModule

trait PomodoroCountdownActorModule extends UIFacadeModule {

  case class StartCountdown(minutes: Int) extends Message
  case class MinutesRemaining(remaining: Int) extends Message
  case object TimerCompleted extends Message
  case object CancelTimer extends Message
  case object TimerCancelled extends Message

  class PomodoroCountdownActor extends Actor with ActorLogging {

    val statusIconActor = context.parent
    
    private val OneMinute = 1 minute // 500 milliseconds // 60 seconds   

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
          statusIconActor ! TimerCompleted
          context.become(timerInactive)
        } 
      case CancelTimer =>
        timer.cancel()
        context.parent ! TimerCancelled
      case x => log.info("  ACTIVE got " + x)
    }

    private def scheduleOneMinuteCallback(currentMinutesRemaining: Int) {      
      statusIconActor ! MinutesRemaining(currentMinutesRemaining)      
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