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