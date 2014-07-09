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
import akka.event.LoggingReceive
import org.gnostai.tomatojuice.ui.actors.TomatoJuiceUIMainModule
import org.gnostai.tomatojuice.persist.PersistModule
import org.gnostai.tomatojuice.core.CoreMessagesModule
import akka.routing.Listen

trait TomatoJuiceMainModule extends CoreMessagesModule with PomodoroTrackerModule with PomodoroCountdownModule {

  this: TomatoJuiceUIMainModule with PersistModule =>

  object TomatoJuiceMain {
    case object StartUI

  }

  class TomatoJuiceMainActor extends Actor with ActorLogging {

    import CoreMessages._
    import TomatoJuiceMain._

    val pomodoroTracker = context.actorOf(Props(new PomodoroTrackerActor(self)), "PomodoroTracker")
    val db = createDBMainActor(context, "TomatoJuiceDB")
    val uiMain = context.actorOf(Props(new TomatoJuiceUIMain(self)), "TomatoJuiceUI")

    override def receive = beforeStart orElse pomodoroListener

    def beforeStart: Receive = LoggingReceive {
      case StartUI =>
        uiMain ! StartUp
        context.become(pomodoroInactive orElse pomodoroListener)
    }

    def pomodoroListener: Receive = LoggingReceive {
      case RegisterPomodoroListener(listener) =>
        pomodoroTracker ! Listen(listener)
      case GetProjectList =>
        val origSender = sender
        db ! SendProjectList(origSender)
      case x @ SendProjectList(origSender) =>
        db ! x
    }

    def pomodoroInactive: Receive = LoggingReceive {
      case StartTimer =>
        pomodoroTracker ! PomodoroTracker.TimerActivated
      case NewPomodoroStarted =>
        db ! RecordPomodoroStart
      case PomodoroPersist.PomodoroCreated(id) =>
        context.become(pomodoroActive(id) orElse pomodoroListener)
    }

    def pomodoroActive(id: POMODORO_ID): Receive = LoggingReceive {
      case ConfirmPomodoroCompleted =>
        db ! RecordPomodoroCompleted(id)
        context.become(pomodoroInactive orElse pomodoroListener)
      case ConfirmPomodoroCompletedWithNote(project: Project, note: String) =>
        db ! PomodoroPersist.SavePomodoroNote(id, project, note)
        context.become(pomodoroInactive orElse pomodoroListener)
    }
  }

}