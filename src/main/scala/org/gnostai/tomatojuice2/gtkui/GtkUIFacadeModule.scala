package org.gnostai.tomatojuice2.gtkui


import org.gnome.gtk
import org.gnostai.tomatojuice2.ui.UIFacadeModule
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


trait GtkUIFacadeModule extends UIFacadeModule {  
  
  type GUI_HANDLE = gtk.Application
  
  protected [gtkui] def safely(thunk: => scala.Unit) = {
    Glib.idleAdd(new Handler() {
      override def run() = {
        println("running thunk...")
        thunk
        false
      }
    })
  }
  
  override def initializeGui(system: ActorSystem, mainApp: ActorRef): GUI_HANDLE = {
    println("initialize gui")
    gtk.Gtk.init(Array()) // TODO see if we need any args here 
    val uniqueApplication = new gtk.Application("org.gnostai.tomatojuice2", 
        ApplicationFlags.NONE);

    uniqueApplication.connect(new gtk.Application.Startup() {
      def onStartup(app: gtk.Application) {
        println("onStartup")
        Notify.init("tomatojuice")       
        Internationalization.init("alexandria", "/usr/share/locale")        
        // Because we don't start with a top-level window active, we must
        // use gtk.Application's hold/unhold mechanism.
        app.hold()
        
        
      }
    })

    uniqueApplication.connect(new gtk.Application.Activate() {
      def onActivate(source: gtk.Application) {
        println("onActivate")
        mainApp ! GuiActivated(source)
        
//        // vv TEMP REMOVE vv
//             val pixbuf = new Pixbuf("src/main/resources/icons/green-led-on.png")
//        val status = new StatusIcon(pixbuf)
//        status.setVisible(true)
//        println(status.isEmbedded())
//        // ^^ TEMP REMOVE ^^
//        
      }
    })
    
    
    //import scala.concurrent.ExecutionContext.Implicits.global
    implicit val dispatcher = system.dispatcher
    val exitCodeFuture = Future { uniqueApplication.run(Array()) }
    
    exitCodeFuture onSuccess {

      // will complete when the last Window is removed from the Application
      case exitCode =>
        println("  exitCode>> " + exitCode)
        mainApp ! "ShutdownUI HACK"
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