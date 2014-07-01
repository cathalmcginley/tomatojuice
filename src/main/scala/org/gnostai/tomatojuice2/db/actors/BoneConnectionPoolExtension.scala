package org.gnostai.tomatojuice2.db.actors

import org.gnostai.tomatojuice2.db.BoneConnectionPool

import akka.actor.ExtendedActorSystem
import akka.actor.Extension
import akka.actor.ExtensionId
import akka.actor.ExtensionIdProvider

class BoneConnectionPoolExtensionImpl(system: ExtendedActorSystem)
  extends Extension with BoneConnectionPool {
  
  //Since this Extension is a shared instance
  // per ActorSystem we need to be threadsafe
  // (in fact the BoneConnectionPool trait does this with lazy vals)
  
  
} 



object BoneConnectionPoolExtension
  extends ExtensionId[BoneConnectionPoolExtensionImpl]
  with ExtensionIdProvider {
  
  //The lookup method is required by ExtensionIdProvider,
  // so we return ourselves here, this allows us
  // to configure our extension to be loaded when
  // the ActorSystem starts up
  override def lookup = BoneConnectionPoolExtension
  
  
 
  //This method will be called by Akka
  // to instantiate our Extension
  override def createExtension(system: ExtendedActorSystem) = new BoneConnectionPoolExtensionImpl(system)
}

