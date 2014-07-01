package org.gnostai.tomatojuice.actors

import akka.actor._
import org.gnostai.tomatojuice.ui.actors.TomatoJuiceUIMainModule
import org.gnostai.tomatojuice.db.actors.TomatoJuiceDatabaseModule

trait TomatoJuiceMainModule {

  this: TomatoJuiceUIMainModule with TomatoJuiceDatabaseModule =>
  
  class TomatoJuiceMainActor extends Actor with ActorLogging {

    val uiMain = context.actorOf(Props(new TomatoJuiceUIMain()), "TomatoJuiceUI")
    val db = createDBMainActor(context, "TomatoJuiceDB")
    
    override def receive: Receive = {
      case x => log.info("main " + x)
    }
  }

}