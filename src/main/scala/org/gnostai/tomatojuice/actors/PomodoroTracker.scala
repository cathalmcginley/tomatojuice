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
import akka.routing.Listeners
import org.gnostai.tomatojuice.core._
import scala.collection.JavaConversions

trait PomodoroTrackerModule extends CoreModule with PomodoroCountdownModule {

  object PomodoroTracker {
    case object TimerActivated
    case class CountdownMinutesRemaining(timer: CountdownTimer, minutes: Int)
    case class CountdownTimerCompleted(timer: CountdownTimer)

  }

  trait PomodoroTrackerActor extends Listeners with ActorLogging { this: Actor =>

    def mainApp: ActorRef
    def countdownActor: ActorRef

    def timerInactive(nextCountdown: CountdownTimer, pomodorosRemaining: Int): Receive
    def countingDown(timer: CountdownTimer, pomodorosRemaining: Int): Receive

    protected def pomodorosBeforeLongBreak: Int

    protected def nextTimerFor(countdown: CountdownTimer, pomodorosRemaining: Int): CountdownTimer

    protected def minutesToCountDown(countdown: CountdownTimer): Int

  }

  trait PomodoroTrackerActorImpl extends PomodoroTrackerActor { this: Actor =>

    import CoreMessages._
    import PomodoroTracker._
    import PomodoroCountdown._

    val pomodoroConfig = config.getConfig("tomatojuice.pomodoro")

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
        System.err.println("> " + TimerCompleted)
        import JavaConversions._
        for (x <- listeners) {
          System.err.println(" + " + x)
        }
         System.err.println(" !!+ " + listeners.size())
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

    protected def pomodorosBeforeLongBreak = pomodoroConfig.getInt("pomodorosBeforeLongBreak")

    protected def nextTimerFor(countdown: CountdownTimer, pomodorosRemaining: Int): CountdownTimer = {
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

    protected def minutesToCountDown(countdown: CountdownTimer) = {
      countdown match {
        case PomodoroCountdownTimer => pomodoroConfig.getInt("duration")
        case ShortBreakCountdownTimer => pomodoroConfig.getInt("breakDuration")
        case LongBreakCountdownTimer => pomodoroConfig.getInt("longBreakDuration")
      }
    }
  }

  class PomodoroTrackerActorProductionImpl(val mainApp: ActorRef) extends Actor with PomodoroTrackerActorImpl {
    val countdownActor = context.actorOf(Props(newPomodoroCountdownActor(self)))
  }

}

trait ProductionPomodoroTrackerModule extends PomodoroTrackerModule
  with PomodoroCountdownModule {

}
