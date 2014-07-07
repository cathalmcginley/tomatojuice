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
        context.become(countingDown(nextCountdown, pomodorosRemaining) orElse listenerManagement)
        val minutes = minutesToCountDown(nextCountdown)
        countdownActor ! StartCountdown(minutes)
        mainApp ! NewPomodoroStarted
    }

    def countingDown(timer: CountdownTimer, pomodorosRemaining: Int): Receive = {
      case MinutesRemaining(mins) =>
        gossip(CountdownMinutesRemaining(timer, mins))
      case TimerCompleted =>
        gossip(CountdownMinutesRemaining(timer, 0))
        val remaining = timer match {
          case PomodoroCountdownTimer => pomodorosRemaining
          case ShortBreakCountdownTimer => pomodorosRemaining - 1
          case LongBreakCountdownTimer => pomodorosBeforeLongBreak
        }
        val nextTimer = nextTimerFor(timer, remaining)
        gossip(CountdownTimerCompleted(nextTimer))
       
        context.become(timerInactive(nextTimer, remaining))
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
