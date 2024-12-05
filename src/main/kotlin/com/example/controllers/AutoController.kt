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
import java.sql.SQLException

class AutoController(private val service: AutoService) {
    fun setupRoutes(routing: Routing) {
        routing.route("/autos") {
            get {
                try {
                    val autos = service.getAllAutos()
                    call.respond(autos)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "An error occurred while fetching autos")
                    )
                }
            }

            get("{id}") {
                val id =
                    call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                try {
                    val auto =
                        service.getAutoById(id)
                            ?: throw NotFoundException("Auto with ID $id not found")
                    call.respond(auto)
                } catch (e: NotFoundException) {
                    throw e
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "An error occurred while fetching the auto")
                    )
                }
            }

            post {
                val autoDto = try {
                    call.receive<AutoDto>()
                } catch (e: Exception) {
                    throw BadRequestException("Invalid auto data")
                }

                try {
                    val addedId = service.addAuto(autoDto)
                    call.respond(
                        HttpStatusCode.Created,
                        mapOf("message" to "Auto added successfully", "id" to addedId)
                    )
                } catch (e: SQLException) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "Database error occurred while adding the auto")
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "An unexpected error occurred while adding the auto")
                    )
                }
            }

            put("{id}") {
                val id =
                    call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                val autoDto = try {
                    call.receive<AutoDto>().copy(id = id)
                } catch (e: Exception) {
                    throw BadRequestException("Invalid auto data")
                }

                try {
                    val updated = service.updateAuto(autoDto)
                    if (updated) {
                        call.respond(
                            HttpStatusCode.OK,
                            mapOf("message" to "Auto updated successfully")
                        )
                    } else {
                        throw NotFoundException("Auto with ID $id not found")
                    }
                } catch (e: SQLException) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "Database error occurred while updating the auto")
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "An unexpected error occurred while updating the auto")
                    )
                }
            }

            delete("{id}") {
                val id =
                    call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                try {
                    val deleted = service.deleteAuto(id)
                    if (deleted) {
                        call.respond(
                            HttpStatusCode.OK,
                            mapOf("message" to "Auto deleted successfully")
                        )
                    } else {
                        throw NotFoundException("Auto with ID $id not found")
                    }
                } catch (e: SQLException) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "Database error occurred while deleting the auto")
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "An unexpected error occurred while deleting the auto")
                    )
                }
            }
        }
    }
}
