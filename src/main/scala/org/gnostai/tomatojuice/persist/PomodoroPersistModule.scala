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

package org.gnostai.tomatojuice.persist

import akka.actor._
import akka.pattern.pipe
import scala.concurrent.Future
import org.gnostai.tomatojuice.core.CoreDomainModule

trait PomodoroPersistModule extends CoreDomainModule {

  type POMODORO_ID

  object PomodoroPersist {

    case class CreateNewPomodoro(durationMinutes: Int, origSender: ActorRef)
    case class PomodoroCreated(pomodoroId: POMODORO_ID)
    case class PomodoroCompleted(pomodoroId: POMODORO_ID)
    case object Continue

    case class SavePomodoroNote(pomodoroId: POMODORO_ID,
      project: Project,
      text: String)

  }

  abstract class PomodoroPersistActor extends Actor with ActorLogging with UnboundedStash {

    private implicit val dispatcher = context.system.dispatcher

    def receive: Receive = free

    def busy: Receive = {
      case PomodoroPersist.Continue =>
        context.become(free)
        unstashAll()
      case x =>
        stash()
    }

    def free: Receive = {
      case PomodoroPersist.CreateNewPomodoro(mins, origSender) =>
        implicit val dispatcher = context.system.dispatcher
        val origSender = sender
        context.become(busy)
        asyncRecordNewPomodoro(mins, origSender) map { _ => PomodoroPersist.Continue } pipeTo self
      case PomodoroPersist.PomodoroCompleted(id) =>
        implicit val dispatcher = context.system.dispatcher
        val origSender = sender
        context.become(busy)
        asyncMarkPomodoroCompleted(id) map { _ => PomodoroPersist.Continue } pipeTo self
        
      case PomodoroPersist.SavePomodoroNote(id, project, note) =>
        implicit val dispatcher = context.system.dispatcher
        val origSender = sender
        context.become(busy)
        asyncAddPomodoroNote(id, project, note) map { _ => PomodoroPersist.Continue } pipeTo self

    }

    def asyncRecordNewPomodoro(duration: Int, origSender: ActorRef): Future[POMODORO_ID]
    def asyncMarkPomodoroCompleted(id: POMODORO_ID): Future[Boolean]
    
    
    def asyncAddPomodoroNote(id: POMODORO_ID, project: Project, text: String): Future[Boolean]
    
  }
}
