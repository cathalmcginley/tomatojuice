package org.gnostai.tomatojuice.ui

import akka.actor._
import org.gnome.gtk.Application
import org.gnostai.tomatojuice.gtkui.MainWindow
import org.gnome.gtk.Gtk
import org.gnome.glib.ApplicationFlags
import org.freedesktop.bindings.Internationalization
import scala.concurrent.Future
import scala.concurrent.duration._
import org.gnome.notify.Notify
import org.gnostai.tomatojuice.ui.PomodoroCountdownActor

object MainWindowActor {

  case object DisplayUI
  case object CloseUI
  case object CloseMainUIWindow
  case object HideStatusIcon
  case object ShutdownUI
  
  case object ActivateStatusIcon
  
  case object PomodoroComplete

  case class Countdown(remaining: Int)

  def main(args: Array[String]): Unit = {
    val system = ActorSystem("TomatoJuice")
    val main = system.actorOf(Props[MainWindowActor], "TomatoJuiceMainWindow")
    main ! MainWindowActor.DisplayUI

  }

}

class MainWindowActor extends Actor {

  import MainWindowActor._

  def receive = awaitInit

  def awaitInit: Receive = {
    case DisplayUI =>
      val (uniq, gtkWin) = buildMainWindow(Array.empty[String])
      
      context.become(mainWindow(uniq, gtkWin))
      
  }

  def mainWindow(uniq: Application, gtkWindow: MainWindow): Receive = {
    case PomodoroComplete =>
      gtkWindow.safely {
        gtkWindow.popUpDialog()
        gtkWindow.displayNotification()
      }
    case CloseUI =>
      self ! CloseMainUIWindow
      self ! HideStatusIcon
      self ! ShutdownUI
      //      palatinaUi ! PalatinaUI.ShutdownApplication
        case CloseMainUIWindow =>
          closeMainUIWindow(uniq, gtkWindow)
        case HideStatusIcon =>
          countdown ! PomodoroCountdownActor.HideIcon
        case ShutdownUI =>
          shutdownUi(gtkWindow)
          //context.become(awaitShutdown)
    case ActivateStatusIcon =>
      println("got activate")
      countdown ! PomodoroCountdownActor.Start
      gtkWindow.safely {
        gtkWindow.displayNotification()
      }
    case x =>
      println("MainWindowActor <- unknown message " + x)

  }

  var countdown: ActorRef = self  // horrible hack
  
  private def closeMainUIWindow(uniq: Application, gtkWindow: MainWindow) {
    gtkWindow.safely {
      gtkWindow.hideAndRemoveWindow(uniq)
    }
  }
  
  private def shutdownUi(gtkWindow: MainWindow) {
    gtkWindow.safely {
      Gtk.mainQuit()
      println("mainQuot")
    }
    println("shutting down actor system")
    context.system.shutdown
  } 
  
  
  private def buildMainWindow(args: Array[String]) = {
    Gtk.init(args) // see if we can do without
    val mainWin = new MainWindow(self)
    val uniqueApplication = new Application("as.palatin.gtkui.MainApplication", ApplicationFlags.NONE);

    uniqueApplication.connect(new Application.Startup() {
      def onStartup(app: Application) {
        System.out.println("app onStartup");
        Notify.init("tomatojuice")
        for (capability <- Notify.getServerCapabilities()) {
          println("Notify:: " + capability)
        }
        
        countdown = context.actorOf(Props(new PomodoroCountdownActor(mainWin.statusIcon)), "CountDown")
        
        
        Internationalization.init("alexandria", "/usr/share/locale")
        /*
         * IMPORTANT: link the main window to the application, or the application
         * will stop immediately after begin started.
         */
        app.addWindow(mainWin.window);
        
        
        /*
         * Because we don't start with a top-level window active, we must
         * use Gtk.Application's hold/release mechanism.
         */
        //app.hold()

      }
    })

    uniqueApplication.connect(new Application.Activate() {
      def onActivate(source: Application) {
        mainWin.activateGui
        //countdown ! PomodoroCountdownActor.StartPomodoro
      }
    })

    implicit val dispatcher = context.system.dispatcher
    val exitCodeFuture = Future { uniqueApplication.run(args) }
    exitCodeFuture onSuccess {

      // will complete when the last Window is removed from the Application
      case exitCode =>
        println("  exitCode>> " + exitCode)
        self ! ShutdownUI
    }
    exitCodeFuture onFailure {
      case ex => println("foo " + ex)
    }

    (uniqueApplication, mainWin)
  }

}