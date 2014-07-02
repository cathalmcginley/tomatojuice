package org.gnostai.tomatojuice.core

trait CoreDomainModule {

  type ImageData = Array[Byte]
  
  case class Project(id: Option[Int],
  name: String,
  description: String,
  icon: Option[ImageData]) {

}
  
}