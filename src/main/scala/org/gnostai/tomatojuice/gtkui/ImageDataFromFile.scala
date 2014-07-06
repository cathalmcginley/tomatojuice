package org.gnostai.tomatojuice.gtkui

import java.io.File
import java.io.FileInputStream

class ImageDataFromFile(val fileName: String) {

  private def readFileData: Array[Byte] = {
    //    val source = scala.io.Source.fromFile(fileName)
    //    val byteArray = source.map(_.toByte).toArray
    //    println(byteArray.length)
    //    source.close()
    //    byteArray

    val file = new File(fileName)
    val in = new FileInputStream(file)
    val bytes = new Array[Byte](file.length.toInt)
    in.read(bytes)
    in.close()
    return bytes

    //val bis = new BufferedInputStream(new FileInputStream(fileName))
    //val bArray = Stream.continually(bis.read).takeWhile(-1 !=).map(_.toByte).toArray
    //bArray
  }

  lazy val data: Array[Byte] = readFileData
}
