package org.gnostai.tomatojuice.mysqldb

import org.gnostai.tomatojuice.db.actors.TomatoJuiceDatabaseModule
import org.gnostai.tomatojuice.db.actors.BoneConnectionPoolExtension
import akka.actor.Props
import akka.actor.ActorContext

trait TomatoJuiceMySQLDBMainModule extends TomatoJuiceDatabaseModule {
  
  def createDBMainActor(context: ActorContext, name: String) = {
    context.actorOf(Props(new TomatoJuiceMySQLDBMain), name)
  }
  
  class TomatoJuiceMySQLDBMain extends TomatoJuiceDBMain {

    val connectionPoolExt = BoneConnectionPoolExtension(context.system)

    val conn = connectionPoolExt.pool.getConnection()
    
    val pomodoroActor = context.actorOf(Props(new PomodoroDatabaseActor(conn)))
    
  }

}