package com.example.configDb

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection
import java.sql.DriverManager

object DataBaseFactory {

    private fun getConnectionParams(): String {
        return "jdbc:postgresql://${DataBaseConfig.host}:${DataBaseConfig.port}/${DataBaseConfig.dbname}"
    }

    private fun connectToDatabase(): Database {
        return Database.connect(
            getConnectionParams(),
            driver = "org.postgresql.Driver",
            user = DataBaseConfig.user,
            password = DataBaseConfig.password
        )
    }

    fun init() {
        connectToDatabase()
    }

    fun getConnection(): Connection {
        return DriverManager.getConnection(
            getConnectionParams(),
            DataBaseConfig.user,
            DataBaseConfig.password
        )
    }

    fun <T> dbQuery(block: () -> T): T {
        return transaction {
            block()
        }
    }
}


