package org.gnostai.tomatojuice.mysqldb

import org.gnostai.tomatojuice.db.actors.DatabaseModule
import org.gnostai.tomatojuice.db.actors.BoneConnectionPoolExtension
import akka.actor.Props
import akka.actor.ActorContext

trait MySQLDatabaseModule extends DatabaseModule {
  
  def createDBMainActor(context: ActorContext, name: String) = {
    context.actorOf(Props(new MySQLDatabaseActor), name)
  }
  
  class MySQLDatabaseActor extends DatabaseActor {

    val connectionPoolExt = BoneConnectionPoolExtension(context.system)

    val conn = connectionPoolExt.pool.getConnection()
    
    val pomodoroActor = context.actorOf(Props(new PomodoroDatabaseActor(conn)))
    
  }

}