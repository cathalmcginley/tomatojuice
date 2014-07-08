/*
 * Copyright (C) 2014 Cathal Mc Ginley
 *
 * This file is part of TomatoJuice, a Pomodoro timer-tracker for GNOME.
 *
 * TomatoJuice is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published
 * by the Free Software Foundation; either version 3 of the License,
 * or (at your option) any later version.
 *
 * TomatoJuice is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with TomatoJuice; see the file COPYING. If not, write to the
 * Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA 02110-1301 USA.
*/

package org.gnostai.tomatojuice.actors

import akka.actor._
import scala.concurrent.Future
import scala.concurrent.duration._
import scala.language.postfixOps
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
