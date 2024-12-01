package com.example.controllers

import com.example.dto.AutoDto
import com.example.services.AutoService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

class AutoController(private val service: AutoService) {
    fun setupRoutes(routing: Routing) {
        routing.route("/autos") {
            get {
                val autos = service.getAllAutos()
                call.respond(autos)
            }

            get("{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                val auto = service.getAutoById(id) ?: throw NotFoundException("Auto with ID $id not found")
                call.respond(auto)
            }

            post {
                val autoDto = call.receive<AutoDto>()
                val addedId = service.addAuto(autoDto)
                call.respond(HttpStatusCode.Created, mapOf("message" to "Auto added successfully", "id" to addedId))
            }

            put("{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                val autoDto = call.receive<AutoDto>().copy(id = id)
                val updated = service.updateAuto(autoDto)
                if (updated) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Auto updated successfully"))
                } else {
                    throw NotFoundException("Auto with ID $id not found")
                }
            }

            delete("{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                val deleted = service.deleteAuto(id)
                if (deleted) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Auto deleted successfully"))
                } else {
                    throw NotFoundException("Auto with ID $id not found")
                }
            }
        }
    }
}
