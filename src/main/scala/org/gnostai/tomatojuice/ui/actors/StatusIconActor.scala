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

package org.gnostai.tomatojuice.ui.actors

import akka.actor._
import org.gnostai.tomatojuice.ui.StatusIconModule
import scala.concurrent.Future
import org.gnostai.tomatojuice.ui.AudioNotificationModule
import org.gnostai.tomatojuice.core.CoreMessagesModule
import org.gnostai.tomatojuice.core.CoreConfigurationModule
import org.gnostai.tomatojuice.actors.PomodoroTrackerModule
import akka.event.LoggingReceive
import org.gnostai.tomatojuice.ui.NoteDialogModule
import org.gnostai.tomatojuice.ui.NoteDialogModule
import org.gnostai.tomatojuice.actors.PomodoroCountdownModule
import org.gnostai.tomatojuice.core.CoreDomainModule
import org.gnostai.tomatojuice.core.CoreModule

trait StatusIconActorModule extends CoreModule with AudioNotificationModule 
  with PomodoroTrackerModule 
     
    
  with StatusIconModule with PomodoroNoteDialogActorModule {

  class StatusIconActor(mainApp: ActorRef) extends Actor with ActorLogging {

    //val pomodoroConfig = config.getConfig("tomatojuice.pomodoro")
    val uiConfig = config.getConfig("tomatojuice.ui")

    import CoreMessages._

    import PomodoroTracker._

    mainApp ! RegisterPomodoroListener(self)

    lazy val audio = createAudioNotification()

    def receive = notInitialized

    def notInitialized: Receive = {
      case DisplayInitialStatusIcon(handle) =>
        log.info("StatusIconActor got DisplayInitialStatusIcon")
        import context.dispatcher
        val iconFuture = constructStatusIcon(self, handle)
        iconFuture onSuccess {
          case iconFacade =>
            println("! got icon facade " + iconFacade)
            context.become(timerInactive(iconFacade, PomodoroCountdownTimer))
            iconFacade.hintMessage("Ready to start " + PomodoroCountdownTimer)
        }
    }

    
    
    /**
     * this is when a countdown timer has finished, but needs to be clicked to
     * confirm that the pomodoro has been completed...
     */
    def timerSuperInactive(iconFacade: STATUS_ICON, nextCountdown: CountdownTimer): Receive = LoggingReceive {
      case StatusIconActivated =>
        log.info("superInactive > activated; TODO pop down dialog if needed...")
        
        context.become(timerInactive(iconFacade, nextCountdown))
        iconFacade.showStartIcon(nextCountdown)
        iconFacade.hintMessage("Ready to start " + nextCountdown)
        mainApp ! ConfirmPomodoroCompleted
        context.parent ! PomodoroNoteDialog.PopDownNoteDialog
    }

    def timerInactive(iconFacade: STATUS_ICON, nextCountdown: CountdownTimer): Receive = {
      case StatusIconActivated =>
        log.info("inactive > activated")
        context.become(countingDown(iconFacade, nextCountdown))
        mainApp ! StartTimer
        iconFacade.hintMessage("Started timer for " + nextCountdown)
        if (uiConfig.getBoolean("soundEffects")) {
          implicit val disp = context.system.dispatcher
          for (facade <- audio) { facade.playInitialPomodoroSound() }
        }
    }

    def countingDown(iconFacade: STATUS_ICON, countdown: CountdownTimer): Receive = {
      case CountdownMinutesRemaining(timer, mins) =>
        iconFacade.showMinutesRemaining(mins, timer)
      case CountdownTimerCompleted(nextTimer) =>
        iconFacade.timerCompleted()

        context.become(timerSuperInactive(iconFacade, nextTimer))
        iconFacade.hintMessage("Finished timer for " + countdown)

        if (uiConfig.getBoolean("soundEffects")) {
          implicit val disp = context.system.dispatcher
          for (facade <- audio) { facade.playPomodoroCompletedSound() }
        }
    }

  }

}