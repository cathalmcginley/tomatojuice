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
import org.gnostai.tomatojuice.core.CoreDomainModule
import org.gnostai.tomatojuice.core.CoreDomainModule
import org.gnostai.tomatojuice.core.CoreDomainModule

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

trait GtkPomodoroNoteDialogModule extends CoreDomainModule
  with GtkProjectListModule
  with NoteDialogModule with GtkUIFacadeModule {

  type POMODORO_NOTE_DIALOG = GtkPomodoroNoteDialog

  override def createNoteDialog(mainUi: ActorRef, handle: ApplicationHandle): Future[POMODORO_NOTE_DIALOG] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val facadePromise = Promise[POMODORO_NOTE_DIALOG]
    safely {
      //val iconFacade = new GtkStatusIconFacade(iconActor, handle)
      val dialogFacade = new GtkPomodoroNoteDialog(mainUi, handle.guiHandle)
      facadePromise success (dialogFacade)
    }
    facadePromise.future
  }

  class GtkPomodoroNoteDialog(mainUi: ActorRef, guiHandle: GUI_HANDLE) extends PomodoroNoteDialogFacade {

    val projectList = new GtkProjectList

    val (noteText, noteTextScroll) = buildNoteText
    val dialog = buildDialog

    def displayProjects(projects: Iterable[Project]) {
      projectList.displayProjects(projects)
    }

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

    private def buildNoteText = {

      val buffer = new TextBuffer()
      val view = new TextView(buffer)
      view.setWrapMode(WrapMode.WORD)
      val scroll = new ScrolledWindow()
      scroll.setPolicy(PolicyType.NEVER, PolicyType.ALWAYS)
      scroll.add(view)
      scroll.setSizeRequest(350, 300) // TODO get this from config

      (view, scroll)
    }

    private def buildDialog: Dialog = {

      val mainVBox = new VBox(false, 0)

      //val menu = new MainMenu

      //mainVBox.packStart(menu.widget, false, false, 3)

      val mainHBox = new HBox(false, 0)

      val vbox = new VBox(false, 0)
      vbox.packStart(noteTextScroll, true, true, 10)

      val buttonPane = new HButtonBox()
      buttonPane.setLayout(ButtonBoxStyle.END)
      val scriptButton = new Button("Save")
      
      scriptButton.connect(new Button.Clicked() {
        def onClicked(source: Button) {
          mainUi ! "Foo"

        }
      })

      buttonPane.add(scriptButton)

      vbox.packEnd(buttonPane, false, false, 5)

      val mainHPane = new HPaned(projectList.widget, vbox)
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
      mainWin.setDefaultSize(550, 400)
      mainWin.setPosition(WindowPosition.MOUSE)

      connectWindowDeleteEvent(mainWin)
      //mainWin.showAll
      //// mainWin.setMaximize(true) // just experimenting
      mainWin
    }

    private def connectWindowDeleteEvent(w: Window) {
      implicit val implicitSender = mainUi

      w.connect(new Window.DeleteEvent() {
        def onDeleteEvent(source: Widget, event: Event): Boolean = {
          println("got Window.DeleteEvent")
          mainUi ! PomodoroNoteDialog.DialogClosing
          false
        }
      })
    }

  }
}