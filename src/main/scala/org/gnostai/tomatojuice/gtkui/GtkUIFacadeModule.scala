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


import org.gnome.gtk
import org.gnostai.tomatojuice.ui.UIFacadeModule
import org.gnome.notify.Notify
import org.gnome.glib.ApplicationFlags
import org.freedesktop.bindings.Internationalization
import akka.actor.ActorRef
import org.gnome.glib.Glib
import org.gnome.glib.Handler
import org.gnome.gdk.Pixbuf
import org.gnome.gtk.StatusIcon
import scala.concurrent.Future
import akka.actor.ActorSystem
import org.gnostai.tomatojuice.core.CoreConfigurationModule


trait GtkUIFacadeModule extends UIFacadeModule 
  with CoreConfigurationModule {  
  
  type GUI_HANDLE = gtk.Application
  
  val gtkuiConfig = config.getConfig("tomatojuice.gtkui")
  
  
  protected [gtkui] def safely(thunk: => scala.Unit) = {
    Glib.idleAdd(new Handler() {
      override def run() = {
        thunk
        false
      }
    })
  }
  
  override def initializeGui(system: ActorSystem, mainUi: ActorRef): GUI_HANDLE = {
    println("initialize gui")
    gtk.Gtk.init(Array()) // TODO see if we need any args here 
    val uniqueApplication = new gtk.Application("org.gnostai.tomatojuice", 
        ApplicationFlags.NONE);

    uniqueApplication.connect(new gtk.Application.Startup() {
      def onStartup(app: gtk.Application) {
        Notify.init("tomatojuice")       
        Internationalization.init("alexandria", "/usr/share/locale")        
        // Because we don't start with a top-level window active, we must
        // use gtk.Application's hold/unhold mechanism (hold/release in Gtk+).
        app.hold()
        
        
      }
    })

    uniqueApplication.connect(new gtk.Application.Activate() {
      def onActivate(source: gtk.Application) {
        println("onActivate " + mainUi.path)
        mainUi ! GuiActivated(source)  
      }
    })
    
    
    //import scala.concurrent.ExecutionContext.Implicits.global
    implicit val dispatcher = system.dispatcher
    val exitCodeFuture = Future { uniqueApplication.run(Array()) }
    
    exitCodeFuture onSuccess {

      // will complete when the last Window is removed from the Application
      case exitCode =>
        mainUi ! "ShutdownUI HACK"
    }
    exitCodeFuture onFailure {
      case ex => println("foo " + ex)
    }

    
    uniqueApplication
  }
  
  override def shutdownGui(mainApp: ActorRef, handle: GUI_HANDLE) {
    handle.unhold()
  }
  
}