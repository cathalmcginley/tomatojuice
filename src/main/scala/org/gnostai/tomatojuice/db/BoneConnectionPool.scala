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

package org.gnostai.tomatojuice.db

import com.jolbox.bonecp.BoneCPDataSource
import org.gnostai.tomatojuice.core.CoreConfigurationModule
import org.gnostai.tomatojuice.core.CoreModule

trait BoneConnectionPool {
  this: CoreConfigurationModule =>
  
  lazy val pool = {
    val ds = new BoneCPDataSource()

    val dbConfig = config.getConfig("tomatojuice.db")
    
    val dbName = dbConfig.getString("databaseName")
    val dbUrl =  dbConfig.getString("url")
    
    ds.setJdbcUrl(dbUrl)
    ds.setUsername(dbConfig.getString("username"))
    ds.setPassword(dbConfig.getString("password"))
    
    // TODO [FIX] this breaks with a ClassNotFoundException
    //     but the connection pool works perfectly without it
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