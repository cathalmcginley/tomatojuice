package org.gnostai.tomatojuice.ui.actors

import akka.actor._
import org.gnostai.tomatojuice.ui.NoteDialogModule
import akka.event.LoggingReceive

trait PomodoroNoteDialogActorModule extends NoteDialogModule {

  class PomodoroNoteDialogActor extends Actor with ActorLogging {

    def receive = dialogNotVisible

    import PomodoroNoteDialog._

    def dialogNotVisible: Receive = LoggingReceive {
      case PopUpDialog(handle) =>
        implicit val dispatcher = context.system.dispatcher
        val noteFacadeFuture = createNoteDialog(self, handle)
        context.become(dialogVisible(handle))
        noteFacadeFuture onSuccess {
          case facade => 
            facade.popUp()
            
        }
    }
    
    def dialogVisible(handle: ApplicationHandle): Receive = LoggingReceive {
      case x => 
        log.info("dialog visible: " + x)
    }
  }

}