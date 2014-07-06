package org.gnostai.tomatojuice.gtkui

import akka.actor.ActorRef
import org.gnome.gtk._
import org.gnome.gdk.Event
import org.gnome.gdk.Pixbuf
import org.gnome.notify.Notification
import org.gnome.notify.Notify
import tomatojuice.JavaSounds
import org.gnostai.tomatojuice.ui.NoteDialogModule
import scala.concurrent.Future
import scala.concurrent.Promise

/*
 * 
 * trait NoteDialogModule extends UIFacadeModule {

  abstract class PomodoroNodeDialogFacade extends UIFacade {
    
    def popUp()
    
  }
  
  type POMODORO_NOTE_DIALOG <: PomodoroNodeDialogFacade
  
  def createNoteDialog(handle: ApplicationHandle): Future[POMODORO_NOTE_DIALOG]
  
}
 */

trait GtkPomodoroNoteDialogModule extends NoteDialogModule with GtkUIFacadeModule {

  type POMODORO_NOTE_DIALOG = GtkPomodoroNoteDialog
  
  override def createNoteDialog(mainUi: ActorRef, handle: ApplicationHandle) : Future[POMODORO_NOTE_DIALOG] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val facadePromise = Promise[POMODORO_NOTE_DIALOG]
    safely {
      //val iconFacade = new GtkStatusIconFacade(iconActor, handle)
      val dialogFacade = new GtkPomodoroNoteDialog(handle.mainApp, handle.guiHandle)
      facadePromise success (dialogFacade)
    }
    facadePromise.future
  }
  
  class GtkPomodoroNoteDialog(mainWindowActor: ActorRef, guiHandle: GUI_HANDLE) extends PomodoroNoteDialogFacade {

    val dialog = buildDialog

//    def activateGui() {
//
//      dialog.present()
//
//    }

    def popUp() {
      println("showing dialog")
      safely {
        dialog.showAll()
        guiHandle.addWindow(dialog)
        dialog.present()
      }
    }
      

    def hideAndRemoveWindow(uniq: Application) {
      dialog.hide()
      uniq.removeWindow(dialog)
    }

    // ------------- [ private methods ] -------------------

    private def buildDialog: Dialog = {

      val mainVBox = new VBox(false, 0)

      //val menu = new MainMenu

      //mainVBox.packStart(menu.widget, false, false, 3)

      val mainHBox = new HBox(false, 0)
      val nav = new GtkProjectList
      val testLabel = new Label("test 2") /* HACK */

      val mainHPane = new HPaned(nav.widget, testLabel)
      mainHPane.setPosition(150)
      mainVBox.add(mainHPane)

      // mainVbox add menubar

      ////val mainMenu = new MainMenu()
      ////mainVBox.packStart(mainMenu.menuBar, false, false, 3);

      // mainVbox add toolbar
      ////val mainHPane = new HPaned(navigationPane.widget, viewPane.widget)
      ////mainHPane.setPosition(150)
      ////mainVBox.add(mainHPane)
      // mainVBox add statusbar

      val mainWin = new Dialog
      mainWin.hide()
      mainWin.setTitle("TomatoJuice")
      //// mainWin.setIcon(Icons.PalatinaIcon)

      //println(org.freedesktop.icons.Helper.getName(org.freedesktop.icons.ApplicationIcon.ACCESSORIES_CALCULATOR ))

      mainWin.add(mainVBox)
      //mainWin.setDefaultSize(1200, 800)
      mainWin.setDefaultSize(800, 600)
      connectWindowDeleteEvent(mainWin)
      //mainWin.showAll
      //// mainWin.setMaximize(true) // just experimenting
      mainWin
    }

    private def connectWindowDeleteEvent(w: Window) {
      implicit val implicitSender = mainWindowActor

      w.connect(new Window.DeleteEvent() {
        def onDeleteEvent(source: Widget, event: Event): Boolean = {
          println("got Window.DeleteEvent")
          mainWindowActor ! "HACK MainWindowActor.CloseUI"
          false
        }
      })
    }

  }
}