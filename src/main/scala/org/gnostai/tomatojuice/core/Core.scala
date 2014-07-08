package org.gnostai.tomatojuice.core

trait CoreModule {
  this: CoreConfigurationModule with CoreDomainModule with CoreMessagesModule =>

}

trait ProductionCoreModule extends CoreModule  
  with ProductionCoreConfigurationModule
  with CoreDomainModule
  with CoreMessagesModule