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
