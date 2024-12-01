package com.example

import com.example.plugins.configureRouting
import com.example.plugins.configureSerialization
import io.ktor.network.tls.certificates.buildKeyStore
import io.ktor.network.tls.certificates.saveToFile
import io.ktor.server.application.Application
import io.ktor.server.engine.ApplicationEngine
import io.ktor.server.engine.applicationEnvironment
import io.ktor.server.engine.connector
import io.ktor.server.engine.embeddedServer
import io.ktor.server.engine.sslConnector
import io.ktor.server.netty.Netty
import org.slf4j.LoggerFactory
import java.io.File


fun main() {
    embeddedServer(
        Netty,
        applicationEnvironment { log = LoggerFactory.getLogger("ktor.application") },
        {
            envConfig()
        },
        module = Application::module
    ).start(wait = true)
}


fun Application.module() {
    configureSerialization()
    configureRouting()
}

private fun ApplicationEngine.Configuration.envConfig() {

    val keyStorePass = System.getenv("KEYSTORE_PASS")
    val certificatePass = System.getenv("CERTIFICATE_PASS")

    val keyStoreFile = File("resources/server.keystore")
    val keyStore = buildKeyStore {
        certificate("sampleAlias") {
            password = certificatePass
            domains = listOf("127.0.0.1", "0.0.0.0", "localhost")
        }
    }
    keyStore.saveToFile(keyStoreFile, keyStorePass)

    connector {
        port = 8080
    }

    sslConnector(
        keyStore = keyStore,
        keyAlias = "sampleAlias",
        keyStorePassword = { keyStorePass.toCharArray() },
        privateKeyPassword = { certificatePass.toCharArray() }) {
        port = 8443
        keyStorePath = keyStoreFile
    }
}