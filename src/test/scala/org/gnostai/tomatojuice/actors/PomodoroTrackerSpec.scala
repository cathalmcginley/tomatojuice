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

import akka.testkit._
import akka.actor._
import org.scalatest._
import org.gnostai.tomatojuice.core._
import com.typesafe.config.ConfigFactory
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import akka.routing.Listen

trait TestPomodoroCountdownModule extends PomodoroCountdownModule {

  implicit def system: ActorSystem // satisfied by extending TestKit

  class TestCountdownActor(val pomodoroTracker: ActorRef) extends Actor with PomodoroCountdownActor {

    override def receive = timerInactive
  }

  lazy val countdown = TestActorRef(new TestCountdownActor(null))

  override def newPomodoroCountdownActor(tracker: ActorRef): Actor =
    countdown.underlyingActor

}

trait TestPomodoroTrackerModule extends PomodoroTrackerModule {
  class TestPomodoroTrackerActor(val mainApp: ActorRef, val countdownActor: ActorRef) extends Actor with PomodoroTrackerActorImpl {

    override def receive = timerInactive(PomodoroCountdownTimer, pomodorosBeforeLongBreak) orElse listenerManagement
  }
}

class PomodoroTrackerSpec extends TestKit(ActorSystem("PomodoroTrackerSpec", TestConfiguration.config))
  with WordSpecLike
  with MustMatchers
  with BeforeAndAfterAll
  with PomodoroTrackerModule
  with TestPomodoroCountdownModule
  with TestPomodoroTrackerModule
  with TestCoreModule {

  import PomodoroTracker._

  override def afterAll() { system.shutdown() }

  "the pomodoro tracker" should {
    "begin with a 23 minute timer" in {
      val testTracker = TestActorRef(new TestPomodoroTrackerActor(testActor, testActor))
      testTracker ! TimerActivated
      expectMsg(PomodoroCountdown.StartCountdown(23))
      expectMsg(CoreMessages.NewPomodoroStarted)
    }

    "register listeners" in {
      val testTracker = TestActorRef(new TestPomodoroTrackerActor(testActor, testActor))
      testTracker.receive(Listen(testActor))  // so we receive the response
      testTracker.receive(TimerActivated)     // to ensure it's `countingDown`
      
      testTracker ! PomodoroCountdown.TimerCompleted
      
      fishForMessage() {
        case PomodoroTracker.CountdownTimerCompleted(ShortBreakCountdownTimer) => true
        case x => false
      }
    }
  }

}