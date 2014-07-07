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

import akka.actor._
import org.gnostai.tomatojuice.db.QueryToStream
import java.sql.ResultSet
import akka.pattern.pipe
import org.gnostai.tomatojuice.core.CoreDomainModule
import java.sql.Connection
import org.gnostai.tomatojuice.persist.ProjectPersistModule
import scala.concurrent.Future
import java.sql.Statement
import java.io.ByteArrayInputStream


trait ProjectDatabaseActorModule extends ProjectPersistModule with CoreDomainModule {

  case class ProjectDbId(dbId: Int)

  type PROJECT_ID = ProjectDbId

  class ProjectDatabaseActor(conn: Connection) extends ProjectPersistActor {

    private implicit val dispatcher = context.system.dispatcher

    private val insertSql = "INSERT INTO project (name, description, icon_png)" +
      " VALUES (?, ?, ?)"
    lazy val insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)

    def asyncGetAllProjects(origSender: ActorRef): Future[Iterable[Project]] = Future {      
      val x = new ProjectDBUtil().loadAll(conn)
      
      println(x)
      
      x
      
    }
    

    def asyncRecordNewProject(name: String, description: String,
      icon: Option[ImageData], origSender: ActorRef): Future[PROJECT_ID] = Future {

      insertStmt.setString(1, name)
      insertStmt.setString(2, description)
      icon map {
        bytes => insertStmt.setBlob(3, new ByteArrayInputStream(bytes))
      } getOrElse {
        insertStmt.setNull(3, java.sql.Types.BLOB)
      }
      val count = insertStmt.executeUpdate()
      val rslt = insertStmt.getGeneratedKeys()
      if (count == 1) {
        rslt.next()
        val newDbId = rslt.getInt(1)
        ProjectDbId(newDbId)
      } else {
        // TODO fail at this point
        throw new java.sql.SQLException("failed to insert 'project' " + name)
      }

    }

  }

  class ProjectDBUtil extends QueryToStream {

    private def rsltToProject(rslt: ResultSet): Project = {
      val id = rslt.getInt(1)
      val name = rslt.getString(2)
      val descr = rslt.getString(3)
      val icon = rslt.getBlob(4)
      val iconBytes: Option[Array[Byte]] = if (icon == null) {
        None
      } else {

        Some(icon.getBytes(0, 100))
      }
      Project(Some(id), name, descr, iconBytes)
    }
    //def makeProjectStream(r: ResultSet): Stream[Project] = makeStream(rsltToProject)(r)

    val makeProjectStream = makeStream(rsltToProject)(_)

    def loadAll(conn: Connection): Seq[Project] = {
      val stmt = conn.createStatement()
      val rslt = stmt.executeQuery("SELECT id, name, description, icon_png FROM project")

      val valueStream = makeProjectStream(rslt)

      valueStream.toArray.toSeq

    }

  }
}