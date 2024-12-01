package com.example.plugins

import com.example.configDb.DataBaseFactory
import com.example.controllers.AutoController
import com.example.controllers.AutoPersonnelController
import com.example.controllers.JournalController
import com.example.controllers.RoutesController
import com.example.controllers.UserController
import com.example.services.AutoPersonnelService
import com.example.services.AutoService
import com.example.services.JournalService
import com.example.services.RoutesService
import com.example.services.UsersService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing


fun Application.configureRouting() {
    install(StatusPages) {
        exception<Throwable> { call, cause ->
            call.respond(HttpStatusCode.InternalServerError, cause.message ?: "Unknown error")
        }
    }

    DataBaseFactory.init()

    routing {
        get("/") {
            call.respond(HttpStatusCode.OK, "Welcome to the Auto Service API")
        }

        val autoService = AutoService()
        val autoController = AutoController(autoService)
        autoController.setupRoutes(this)

        val personnelService = AutoPersonnelService()
        val personnelController = AutoPersonnelController(personnelService)
        personnelController.setupRoutes(this)

        val routesService = RoutesService()
        val routesController = RoutesController(routesService)
        routesController.setupRoutes(this)

        val journalService = JournalService()
        val journalController = JournalController(journalService)
        journalController.setupRoutes(this)

        val usersService = UsersService()
        val userController = UserController(usersService)
        userController.setupRoutes(this)

    }
}

