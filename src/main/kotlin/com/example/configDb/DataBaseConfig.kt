package com.example.configDb

import java.util.Properties

object DataBaseConfig {
    private const val CONFIG_FILE = "/config.ini"
    private val properties: Properties = Properties()

    init {
        try {
            val inputStream = this::class.java.getResourceAsStream(CONFIG_FILE)
                ?: throw IllegalStateException("Cannot find $CONFIG_FILE")

            properties.load(inputStream)
        } catch (e: Exception) {
            println("Config file load error: ${e.message}")
            e.printStackTrace()
        }
    }

    val host: String get() = properties.getProperty("host")
    val port: String get() = properties.getProperty("port")
    val dbname: String get() = properties.getProperty("dbname")
    val user: String get() = properties.getProperty("user")
    val password: String
        get() = System.getenv("DB_PASS") ?: properties.getProperty(
            "password", System.getenv("KEYSTORE_PASS")
        )
}