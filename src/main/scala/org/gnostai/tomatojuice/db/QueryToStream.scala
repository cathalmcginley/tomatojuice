package org.gnostai.tomatojuice.db

import java.sql.ResultSet


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