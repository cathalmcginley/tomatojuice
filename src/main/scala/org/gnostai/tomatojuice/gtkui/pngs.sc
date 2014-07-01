package org.gnostai.tomatojuice2.gtkui

object pngs {
  println("Welcome to the Scala worksheet")       //> Welcome to the Scala worksheet
  
  
  
  val x = 0 to 25                                 //> x  : scala.collection.immutable.Range.Inclusive = Range(0, 1, 2, 3, 4, 5, 6,
                                                  //|  7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25)

	"%02d.png".format(0)                      //> res0: String = 00.png
	val foo = 32                              //> foo  : Int = 32
	f"$foo%03d.png..."                        //> res1: String = 032.png...
	
	for (i <- x) yield f"$i%02d.png"          //> res2: scala.collection.immutable.IndexedSeq[String] = Vector(00.png, 01.png,
                                                  //|  02.png, 03.png, 04.png, 05.png, 06.png, 07.png, 08.png, 09.png, 10.png, 11.
                                                  //| png, 12.png, 13.png, 14.png, 15.png, 16.png, 17.png, 18.png, 19.png, 20.png,
                                                  //|  21.png, 22.png, 23.png, 24.png, 25.png)
	
	//u(23)
  
}