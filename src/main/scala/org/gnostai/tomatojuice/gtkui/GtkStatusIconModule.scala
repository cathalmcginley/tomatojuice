package org.gnostai.tomatojuice.gtkui

import org.gnostai.tomatojuice.ui.StatusIconModule
import org.gnome.gdk
import org.gnome.gtk
import akka.actor.ActorRef
import scala.concurrent.Future
import scala.concurrent.Promise

trait GtkStatusIconModule extends GtkUIFacadeModule with StatusIconModule {

  class GtkStatusIconFacade(iconActor: ActorRef, handle: GUI_HANDLE) extends StatusIconFacade {

    val ImageDir = "src/main/resources/icons"

    val icons = new PomodoroIcons(ImageDir)

    val statusIcon: gtk.StatusIcon = buildStatusIcon

    def sessionBegins() {
      println("(): NO ACTION")
    }

    def breakBegins() {
      println("breakBegins(): NO ACTION")
    }

    def longBreakBegins() {
      println("longBreakBegins(): NO ACTION")
    }

    def showMinutesRemaining(minutesRemaining: Int, countdown: CountdownType) {
      println("showMinutesRemaining(): NO ACTION")
      val icon = iconFor(minutesRemaining, countdown)
      safely {
        statusIcon.setFromPixbuf(icon)
      }
    }
    
    

    private def iconFor(index: Int, countdown: CountdownType) = {
      val icons = iconsForCountdown(countdown)
      if (index < icons.size) {
        icons(index)
      } else {
        icons(0)
      }
    }
    
    private def iconsForCountdown(countdown: CountdownType): Seq[gdk.Pixbuf] = {
      countdown match {
        case PomodoroCountdown => icons.pomodoroIcons
        case ShortBreakCountdown => icons.breakIcons
        case LongBreakCountdown => icons.pomodoroIcons // HACK
      }
    }

    def hintTimeRemaining(minutes: Int, seconds: Int) {
      println("hintTimeRemaining(): NO ACTION")
    }

    def timerCompleted() {
      println("timerCompleted(): NO ACTION")
    }

    private def buildStatusIcon = {

      println("buildStatusIcon")

      //      val mainWin = new gtk.Window
      //        mainWin.add(new gtk.Label("test"))
      //        mainWin.showAll()
      //
      //        handle.addWindow(mainWin)
      //        mainWin.present()

      var status2: gtk.StatusIcon = null
      println("ready for safety")
      //safely {
      println("saef??")
      val status = new gtk.StatusIcon(icons.initial) //icons.initial)
      status.setVisible(true)
      println(status.isEmbedded())

      status.setVisible(true)
      println(status.isEmbedded())

      status.connect(new gtk.StatusIcon.Activate() {
        def onActivate(icon: gtk.StatusIcon) {
          println("activate")
          iconActor ! StatusIconActivated
        }
      })

      println("foo")

      status2 = status
      //}
      status2
    }

  }

  type STATUS_ICON = GtkStatusIconFacade

  override def constructStatusIcon(iconActor: ActorRef, handle: GUI_HANDLE): Future[STATUS_ICON] = {
    println("constructStatusIcon")
    //    safely {
    //        // vv TEMP REMOVE vv
    //             val pixbuf = new gdk.Pixbuf("src/main/resources/icons/green-led-on.png")
    //        val status = new gtk.StatusIcon(pixbuf)
    //        status.setVisible(true)
    //        println(">> embedded?    " + status.isEmbedded())
    //        // ^^ TEMP REMOVE ^^
    //    }



    import scala.concurrent.ExecutionContext.Implicits.global
    val facadePromise = Promise[STATUS_ICON]
    Future {
      safely {
        val iconFacade = new GtkStatusIconFacade(iconActor, handle)
        facadePromise success (iconFacade)
      }
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