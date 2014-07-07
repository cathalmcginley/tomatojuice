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

package org.gnostai.tomatojuice.db

import java.sql.ResultSet
import java.sql.Connection

trait QueryToStream {

  def makeStream[T](thunk: ResultSet => T)(rslt: ResultSet): Stream[T] = {
    
    def rsltToOptionT(): Option[T] = {
      if (rslt.next()) {
        Some(thunk(rslt))
      } else {
        None
      }
    }
    
    def mkStream: Stream[T] = {
      val next = rsltToOptionT()
      next map { x: T => Stream.cons(x, mkStream) } getOrElse (Stream.empty)
    }

    mkStream
  }


}


object QueryMainX extends App with BoneConnectionPool {
  
  
    type ImageData = Array[Byte]

  case class Project(id: Option[Int],
    name: String,
    description: String,
    icon: Option[ImageData]) {

  }
  
   class ProjectDBUtilX extends QueryToStream {

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

      valueStream.toSeq

    }

  }
  
   val conn = pool.getConnection()
   val util = new ProjectDBUtilX()
   val stmt = conn.createStatement()
   val rslt = stmt.executeQuery("SELECT id, name, description, icon_png FROM project")

   //while (rslt.next()) {
//     println(rslt.getString(2))
   //}
   
   val stream = util.makeProjectStream(rslt)
   val xeq = stream.toArray.toSeq
   println(xeq)
   //println(stream.tail.head)
   
   //println(util.loadAll(conn))
   
} 