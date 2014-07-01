package org.gnostai.tomatojuice.db

import com.jolbox.bonecp.BoneCPDataSource

trait BoneConnectionPool {
  lazy val pool = {
    val ds = new BoneCPDataSource()

    val dbName = "tomatojuice"
    val dbUrl = s"jdbc:mysql://localhost/${dbName}"
    ds.setJdbcUrl(dbUrl)

    //ds.setDriverClass("com.mysql.jcbc.Driver")
    ds.setUsername("tomato")
    ds.setPassword("BappyoShno")
    ds setPartitionCount 4
    ds setMaxConnectionsPerPartition 5
    ds setMinConnectionsPerPartition 3
    ds setStatisticsEnabled true
    ds setStatementsCacheSize 50
    ds setServiceOrder "LIFO"

    ds
  }

}