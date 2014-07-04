package org.gnostai.tomatojuice.db

import com.jolbox.bonecp.BoneCPDataSource
import org.gnostai.tomatojuice.core.CoreConfigurationModule

trait BoneConnectionPool extends CoreConfigurationModule {
  lazy val pool = {
    val ds = new BoneCPDataSource()

    val dbConfig = config.getConfig("tomatojuice.db")
    
    val dbName = dbConfig.getString("databaseName")
    val dbUrl =  dbConfig.getString("url")
    
    ds.setJdbcUrl(dbUrl)
    ds.setUsername(dbConfig.getString("username"))
    ds.setPassword(dbConfig.getString("password"))
    
    // FIX this breaks with ClassNotFoundException
    //     but works perfectly without it
    // ds.setDriverClass(dbConfig.getString("driverClass"))  
    
    // TODO take these from the config file too...
    ds setPartitionCount 4
    ds setMaxConnectionsPerPartition 5
    ds setMinConnectionsPerPartition 3
    ds setStatisticsEnabled true
    ds setStatementsCacheSize 50
    ds setServiceOrder "LIFO"

    ds
  }

}