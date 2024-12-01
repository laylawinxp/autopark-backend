package com.example.controllers

import com.example.dto.AutoPersonnelDto
import com.example.services.AutoPersonnelService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

class AutoPersonnelController(private val service: AutoPersonnelService) {
    fun setupRoutes(routing: Routing) {
        routing.route("/personnel") {
            get {
                val personnels = service.getAllPersonnel()
                call.respond(personnels)
            }

            get("{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                val personnel = service.getPersonnelById(id) ?: throw NotFoundException("Personnel with ID $id not found")
                call.respond(personnel)
            }

            post {
                val personnelDto = call.receive<AutoPersonnelDto>()
                val addedId = service.addPersonnel(personnelDto)
                call.respond(HttpStatusCode.Created, mapOf("message" to "Personnel added successfully", "id" to addedId))
            }

            put("{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                val personnelDto = call.receive<AutoPersonnelDto>().copy(id = id)
                val updated = service.updatePersonnel(personnelDto)
                if (updated) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Personnel updated successfully"))
                } else {
                    throw NotFoundException("Personnel with ID $id not found")
                }
            }

            delete("{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                val deleted = service.deletePersonnel(id)
                if (deleted) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Personnel deleted successfully"))
                } else {
                    throw NotFoundException("Personnel with ID $id not found")
                }
            }
        }
    }
}

