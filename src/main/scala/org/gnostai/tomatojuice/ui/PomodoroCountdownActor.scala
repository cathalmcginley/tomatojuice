package org.gnostai.tomatojuice.ui

import akka.actor._
import scala.concurrent.duration._
import org.gnostai.tomatojuice.gtkui.PomodoroCountdownIcon
import scala.concurrent.Future

object PomodoroCountdownActor {

  case object Start
  
  case object StartPomodoro
  case class CountDownPomodoro(minsRemaining: Int)
  case object FinishedPomodoro

  case object StartBreak
  case class CountDownBreak(minsRemaining: Int)

  
  case object FinishedBreak
  
  case object NotifyCompletion

  case object HideIcon
  
}

class PomodoroCountdownActor(facade: PomodoroCountdownIcon) extends Actor {

  import PomodoroCountdownActor._

  val OneMinute = 60 seconds
  val PomodoroDuration = 25
  val BreakDuration = 5

  var lastTimer: Option[Cancellable] = None

  def receive = resetTimer orElse awaitingCountDownPomodoro

  def resetTimer: Receive = {
    case StartPomodoro =>
      for (c <- lastTimer) c.cancel()
      context.become(resetTimer orElse countDownPomodoro)
      self ! CountDownPomodoro(PomodoroDuration)
    case StartBreak =>
      println("start break")
      for (c <- lastTimer) c.cancel()
      context.become(resetTimer orElse countDownBreak)
      self ! CountDownBreak(BreakDuration)
    case NotifyCompletion =>
      facade.safely {
        facade.playSound()
      }
    case HideIcon =>
      facade.safely {
        facade.hideIcon()
      }
  }

  def awaitingCountDownPomodoro: Receive = {
    case Start => 
      context.become(resetTimer orElse countDownPomodoro)
      self ! CountDownPomodoro(PomodoroDuration)
  }
  
  def countDownPomodoro: Receive = {
    case Start =>
      println("Pomodoro:: ignoring re-Start")
    case CountDownPomodoro(remaining) =>
      import context.dispatcher
      if (remaining > 0) {
        lastTimer = Some(context.system.scheduler.scheduleOnce(OneMinute, self, CountDownPomodoro(remaining - 1)))
        Future {
          facade.safely {
            facade.showPomodoroMinutesRemaining(remaining)
          }
        }
      } else {
        lastTimer = None
        Future {
          facade.safely {
            facade.showPomodoroMinutesRemaining(0)
          }
        }
        context.become(resetTimer orElse awaitingCountDownBreak)
        self ! NotifyCompletion
      }

  }

  def awaitingCountDownBreak: Receive = {
    case Start => 
      context.become(resetTimer orElse countDownBreak)
      self ! CountDownBreak(BreakDuration)
  }
  
  def countDownBreak: Receive = {
    case Start =>
      println("Break:: ignoring re-Start")
    case CountDownBreak(remaining) =>
      import context.dispatcher
      if (remaining > 0) {
        lastTimer = Some(context.system.scheduler.scheduleOnce(OneMinute, self, CountDownBreak(remaining - 1)))
        Future {
          facade.safely {
            facade.showBreakMinutesRemaining(remaining)
          }
        }
      } else {
        lastTimer = None
        Future {
          facade.safely {
            facade.showBreakMinutesRemaining(0)
          }
        }
        context.become(resetTimer orElse awaitingCountDownPomodoro)
        self ! NotifyCompletion
      }
  }
}