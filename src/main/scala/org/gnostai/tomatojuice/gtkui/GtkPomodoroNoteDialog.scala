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
import org.gnostai.tomatojuice.ui.actors.PomodoroNoteDialogActorModule

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
  with PomodoroNoteDialogActorModule
  with NoteDialogModule with GtkUIFacadeModule {

  type POMODORO_NOTE_DIALOG = GtkPomodoroNoteDialog

  override def createNoteDialog(mainUi: ActorRef, handle: ApplicationHandle): Future[POMODORO_NOTE_DIALOG] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    val facadePromise = Promise[POMODORO_NOTE_DIALOG]
    safely {
      println("safely")
      //val iconFacade = new GtkStatusIconFacade(iconActor, handle)
      val dialogFacade = new GtkPomodoroNoteDialog(mainUi, handle.guiHandle)
      println("still in safety...")
      facadePromise success (dialogFacade)
    }
    facadePromise.future
  }

  class GtkPomodoroNoteDialog(mainUi: ActorRef, guiHandle: GUI_HANDLE) extends PomodoroNoteDialogFacade {

    println("building")
    
    val projectList = new GtkProjectList

    println("building 2")
    
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
    
    def popDown() {
      println("hiding dialog")
      safely {
        dialog.hide()
        dialog.destroy()
        guiHandle.removeWindow(dialog)
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
          val projectOpt = projectList.getSelectedProject
          val project = projectOpt.getOrElse(Project(Some(0), "Default", "-", None))
                    
          mainUi ! PomodoroNoteDialogActor.Save(
              project,
              noteText.getBuffer().getText())
              
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