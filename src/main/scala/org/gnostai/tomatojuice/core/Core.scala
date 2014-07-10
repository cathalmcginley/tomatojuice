package org.gnostai.tomatojuice.core

trait CoreModule extends CoreConfigurationModule with CoreDomainModule with CoreMessagesModule {

}

trait ProductionCoreModule extends CoreModule  
  with ProductionCoreConfigurationModule
