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

import org.gnostai.tomatojuice.db.BoneConnectionPool
import akka.actor.ExtendedActorSystem
import akka.actor.Extension
import akka.actor.ExtensionId
import akka.actor.ExtensionIdProvider
import org.gnostai.tomatojuice.core.ProductionCoreModule

class BoneConnectionPoolExtensionImpl(system: ExtendedActorSystem)
  extends Extension with BoneConnectionPool with ProductionCoreModule {
  
  //Since this Extension is a shared instance
  // per ActorSystem we need to be threadsafe
  // (in fact the BoneConnectionPool trait does this with lazy vals)
  
  
} 



object BoneConnectionPoolExtension
  extends ExtensionId[BoneConnectionPoolExtensionImpl]
  with ExtensionIdProvider {
  
  // The lookup method is required by ExtensionIdProvider,
  // so we return ourselves here, this allows us
  // to configure our extension to be loaded when
  // the ActorSystem starts up
  override def lookup = BoneConnectionPoolExtension
  
  
 
  // This method will be called by Akka
  // to instantiate our Extension
  override def createExtension(system: ExtendedActorSystem) = new BoneConnectionPoolExtensionImpl(system)
}

