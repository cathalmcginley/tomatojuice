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

trait CoreDomainModule {

  sealed trait CountdownTimer
  case object PomodoroCountdownTimer extends CountdownTimer {
    override def toString = "Pomodoro timer"
  }
  case object ShortBreakCountdownTimer extends CountdownTimer {
    override def toString = "short break"
  }
  case object LongBreakCountdownTimer extends CountdownTimer {
    override def toString = "extended break"
  }

  type ImageData = Array[Byte]

  case class Project(id: Option[Int],
    name: String,
    description: String,
    icon: Option[ImageData]) {

  }

}

// TODO  [QUERY]  is this needed?
object CoreDomainModule extends CoreDomainModule {
  
}