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
import java.sql.SQLException

class AutoPersonnelController(private val service: AutoPersonnelService) {
    fun setupRoutes(routing: Routing) {
        routing.route("/personnel") {
            get {
                try {
                    val personnel = service.getAllPersonnel()
                    call.respond(personnel)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "An error occurred while fetching personnel")
                    )
                }
            }

            get("{id}") {
                val id =
                    call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                try {
                    val personnel = service.getPersonnelById(id)
                        ?: throw NotFoundException("Personnel with ID $id not found")
                    call.respond(personnel)
                } catch (e: NotFoundException) {
                    throw e
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "An error occurred while fetching the personnel")
                    )
                }
            }

            post {
                try {
                    val personnelDto = call.receive<AutoPersonnelDto>()
                    val addedId = service.addPersonnel(personnelDto)
                    call.respond(
                        HttpStatusCode.Created,
                        mapOf("message" to "Personnel added successfully", "id" to addedId)
                    )
                } catch (e: SQLException) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "Database error occurred while adding personnel")
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "An unexpected error occurred while adding personnel")
                    )
                }
            }

            put("{id}") {
                val id =
                    call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                try {
                    val personnelDto = call.receive<AutoPersonnelDto>().copy(id = id)
                    val updated = service.updatePersonnel(personnelDto)
                    if (updated) {
                        call.respond(
                            HttpStatusCode.OK,
                            mapOf("message" to "Personnel updated successfully")
                        )
                    } else {
                        throw NotFoundException("Personnel with ID $id not found")
                    }
                } catch (e: SQLException) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "Database error occurred while updating personnel")
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "An unexpected error occurred while updating personnel")
                    )
                }
            }

            delete("{id}") {
                val id =
                    call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                try {
                    val deleted = service.deletePersonnel(id)
                    if (deleted) {
                        call.respond(
                            HttpStatusCode.OK,
                            mapOf("message" to "Personnel deleted successfully")
                        )
                    } else {
                        throw NotFoundException("Personnel with ID $id not found")
                    }
                } catch (e: TriggerException) {
                    call.respond(
                        HttpStatusCode.Conflict,
                        mapOf("message" to e.message)
                    )
                } catch (e: SQLException) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "Database error occurred while deleting personnel")
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "An unexpected error occurred while deleting personnel")
                    )
                }
            }
        }
    }
}
