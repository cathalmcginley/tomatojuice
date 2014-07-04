package org.gnostai.tomatojuice.ui.actors

import akka.actor._
import org.gnostai.tomatojuice.ui.StatusIconModule
import scala.concurrent.Future
import org.gnostai.tomatojuice.ui.AudioNotificationModule
import org.gnostai.tomatojuice.core.CoreMessagesModule
import org.gnostai.tomatojuice.core.CoreConfigurationModule
import org.gnostai.tomatojuice.actors.PomodoroTrackerModule
import akka.event.LoggingReceive

trait StatusIconActorModule extends PomodoroCountdownActorModule
  with StatusIconModule
  with AudioNotificationModule
  with CoreMessagesModule
  with PomodoroTrackerModule
  with CoreConfigurationModule {

  class StatusIconActor(mainApp: ActorRef) extends Actor with ActorLogging {

    //val pomodoroConfig = config.getConfig("tomatojuice.pomodoro")
    val uiConfig = config.getConfig("tomatojuice.ui")

    import CoreMessages._
    
    import PomodoroTracker._

    val countdownActor = context.actorOf(Props(new PomodoroCountdownActor))

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
        log.info("activated - TODO start " + nextCountdown + " timer or whatever")
        context.become(timerInactive(iconFacade, nextCountdown))
        iconFacade.showStartIcon(nextCountdown)
        iconFacade.hintMessage("Ready to start " + nextCountdown)
        mainApp ! ConfirmPomodoroCompleted

    }

    
    def timerInactive(iconFacade: STATUS_ICON, nextCountdown: CountdownTimer): Receive = {
      case StatusIconActivated =>
        log.info("activated - TODO start " + nextCountdown + " timer or whatever")
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