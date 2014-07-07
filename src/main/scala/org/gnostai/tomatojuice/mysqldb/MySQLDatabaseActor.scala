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
    
    val projectActor = context.actorOf(Props(new ProjectDatabaseActor(conn)))
    
  }

}