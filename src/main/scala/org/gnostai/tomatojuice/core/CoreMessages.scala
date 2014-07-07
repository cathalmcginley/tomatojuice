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

package org.gnostai.tomatojuice.core

import akka.actor.ActorRef

trait CoreMessagesModule extends CoreDomainModule {

  object CoreMessages {

    /** sent to main app by gui; instruct it to notify the tracker  */
    case object StartTimer

    /** sent to main app by tracker; ask main to record this is db */
    case object NewPomodoroStarted

    /** sent to main app by gui again; ask main to update db  */
    case object ConfirmPomodoroCompleted

    /**
     * sent to main to link up parts of the UI actor system with
     * the pomodoro tracker actor
     */
    case class RegisterPomodoroListener(listener: ActorRef) // HACK

    case object GetProjectList
    case class SendProjectList(orig: ActorRef)
    case class ProjectList(projects: Iterable[Project])

    case class ConfirmPomodoroCompletedWithNote(project: Project, note: String)

    case object Quit
  }

}