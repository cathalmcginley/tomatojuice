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

package org.gnostai.tomatojuice.db.actors

import akka.actor.Actor
import java.sql.Connection
import java.sql.Timestamp
import java.sql.Statement
import scala.concurrent.Future
import akka.actor.ActorLogging
import akka.actor.UnboundedStash
import akka.pattern.pipe
import akka.actor.ActorRef
import org.gnostai.tomatojuice.persist.PomodoroPersistModule

trait PomodoroDatabaseModule extends PomodoroPersistModule {

  case class PomodoroDbId(val dbId: Int)

  type POMODORO_ID = PomodoroDbId

  class PomodoroDatabaseActor(conn: Connection) extends PomodoroPersistActor {

    private implicit val dispatcher = context.system.dispatcher

    private val insertSql = "INSERT INTO pomodoro (start_time, duration_minutes, completed)" +
      " VALUES (?, ?, FALSE)"
    lazy val insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)

    private val updateSql = "UPDATE pomodoro SET completed=? WHERE id=?"
    lazy val updateStmt = conn.prepareStatement(updateSql)

    private val noteInsertSql = "INSERT INTO pomodoro_log (pomodoro_id, project_id, description)" +
      " VALUES (?, ?, ?)"
    lazy val noteInsertStmt = conn.prepareStatement(noteInsertSql)

    override def asyncRecordNewPomodoro(mins: Int, origSender: ActorRef) = Future {
      insertStmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()))
      insertStmt.setInt(2, mins)
      val count = insertStmt.executeUpdate()
      val rslt = insertStmt.getGeneratedKeys()
      if (count == 1) {
        rslt.next()
        val newId = rslt.getInt(1)
        val grandparent = context.actorSelection("../../")

        val main = context.actorFor("akka://TomatoJuice/user/TomatoJuice")
        main ! PomodoroPersist.PomodoroCreated(PomodoroDbId(newId))
      }
      PomodoroDbId(count)
    }

    override def asyncMarkPomodoroCompleted(id: POMODORO_ID) = Future {
      updateStmt.setBoolean(1, true)
      updateStmt.setInt(2, id.dbId)
      val count = updateStmt.executeUpdate()
      (count == 1)
    }

    def asyncAddPomodoroNote(id: POMODORO_ID, project: Project, text: String) = Future {
      noteInsertStmt.setInt(1, id.dbId)
      noteInsertStmt.setInt(2, project.id.get)
      noteInsertStmt.setString(3, text)
      val count = noteInsertStmt.executeUpdate()
      (count == 1)
    }

  }

}