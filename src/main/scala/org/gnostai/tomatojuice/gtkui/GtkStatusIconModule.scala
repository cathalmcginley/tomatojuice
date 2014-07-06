package org.gnostai.tomatojuice.gtkui

import org.gnostai.tomatojuice.ui.StatusIconModule
import org.gnome.gdk
import org.gnome.gtk
import akka.actor.ActorRef
import scala.concurrent.Future
import scala.concurrent.Promise
import org.gnostai.tomatojuice.core.CoreConfigurationModule
import java.io.File

trait GtkStatusIconModule extends GtkUIFacadeModule {
   this:StatusIconModule =>


  class GtkStatusIconFacade(iconActor: ActorRef, handle: GUI_HANDLE) extends StatusIconFacade {

    
    val ShareDir = new File(gtkuiConfig.getString("shareDir"))
    val IconsDir = new File(ShareDir, "icons")
    
    val icons = new GtkPomodoroIcons(IconsDir)

    val statusIcon: gtk.StatusIcon = buildStatusIcon

    def sessionBegins() {
      println("(): NO ACTION") // TODO some action here
    }

    def breakBegins() {
      println("breakBegins(): NO ACTION") // TODO some action here
    }

    def longBreakBegins() {
      println("longBreakBegins(): NO ACTION") // TODO some action here
    }

    def showMinutesRemaining(minutesRemaining: Int, countdown: CountdownTimer) {
      safely {
        val icon = iconFor(minutesRemaining, countdown)
        statusIcon.setFromPixbuf(icon)
      }
    }
    
    def showStartIcon(countdown: CountdownTimer) {      
      safely {
        val icon = startIconForCountdown(countdown)
        statusIcon.setFromPixbuf(icon)
      }
    }

    private def iconFor(index: Int, countdown: CountdownTimer) = {
      val icons = iconsForCountdown(countdown)
      if (index < icons.size) {
        icons(index)
      } else {
        icons(0)
      }
    }

    private def iconsForCountdown(countdown: CountdownTimer): Seq[gdk.Pixbuf] = {
      countdown match {
        case PomodoroCountdownTimer => icons.pomodoroIcons
        case ShortBreakCountdownTimer => icons.breakIcons
        case LongBreakCountdownTimer => icons.longBreakIcons 
      }
    }
    
    private def startIconForCountdown(countdown: CountdownTimer): gdk.Pixbuf = {
      countdown match {
        case PomodoroCountdownTimer => icons.pomodoroStartIcon
        case ShortBreakCountdownTimer => icons.breakStartIcon
        case LongBreakCountdownTimer => icons.longBreakStartIcon 
      }
    }

    def hintTimeRemaining(minutes: Int, seconds: Int) {
      val message = f"$minutes%02d:$seconds%02d remaining"
      hintMessage(message)
    }
    
    def hintMessage(message: String) {
      safely {
        statusIcon.setTooltipText(message)
      }
    }
    

    def timerCompleted() {
      println("timerCompleted(): NO ACTION") // TODO some action here
    }

    private def buildStatusIcon = {
      val status = new gtk.StatusIcon(icons.initial)
      status.setVisible(true)
      status.connect(new gtk.StatusIcon.Activate() {
        def onActivate(icon: gtk.StatusIcon) {
          iconActor ! StatusIconActivated
        }
      })
      status
    }

  }

  type STATUS_ICON = GtkStatusIconFacade

  override def constructStatusIcon(iconActor: ActorRef, handle: GUI_HANDLE): Future[STATUS_ICON] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val facadePromise = Promise[STATUS_ICON]
    safely {
      val iconFacade = new GtkStatusIconFacade(iconActor, handle)
      facadePromise success (iconFacade)
    }
    facadePromise.future
  }

  class GtkStatusIconMenuFacade extends StatusIconMenuFacade {
    def popUp() {
      println("popUp(): NO ACTION")
    }
  }

  type STATUS_ICON_MENU = GtkStatusIconMenuFacade

}