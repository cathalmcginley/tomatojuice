package org.gnostai.tomatojuice.db

object StreamStuff {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  
  
  val str: Stream[String] = Stream.cons("foo", Stream.empty)
                                                  //> str  : Stream[String] = Stream(foo, ?)
  
  str.print                                       //> foo, empty
  
  str.head                                        //> res0: String = foo
  
  str.tail                                        //> res1: scala.collection.immutable.Stream[String] = Stream()
 
  str.tail.head                                   //> java.util.NoSuchElementException: head of empty stream
                                                  //| 	at scala.collection.immutable.Stream$Empty$.head(Stream.scala:1058)
                                                  //| 	at scala.collection.immutable.Stream$Empty$.head(Stream.scala:1056)
                                                  //| 	at org.gnostai.tomatojuice.db.StreamStuff$$anonfun$main$1.apply$mcV$sp(o
                                                  //| rg.gnostai.tomatojuice.db.StreamStuff.scala:15)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$$anonfun$$exe
                                                  //| cute$1.apply$mcV$sp(WorksheetSupport.scala:76)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$.redirected(W
                                                  //| orksheetSupport.scala:65)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$.$execute(Wor
                                                  //| ksheetSupport.scala:75)
                                                  //| 	at org.gnostai.tomatojuice.db.StreamStuff$.main(org.gnostai.tomatojuice.
                                                  //| db.StreamStuff.scala:3)
                                                  //| 	at org.gnostai.tomatojuice.db.StreamStuff.main(org.gnostai.tomatojuice.d
                                                  //| b.StreamStuff.scala)
  
}