package com.example.controllers

import com.example.dto.RoutesDto
import com.example.services.RoutesService
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

class RoutesController(private val service: RoutesService) {

    fun setupRoutes(routing: Routing) {
        routing.route("/routes") {
            get {
                try {
                    val routes = service.getAllRoutes()
                    call.respond(routes)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "An error occurred while fetching routes")
                    )
                }
            }

            get("{id}/currently-in-route") {
                val id =
                    call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                try {
                    val result = service.getCurrentlyInRoute(id)
                    call.respond(HttpStatusCode.OK, mapOf("count" to result))
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        "Error while fetching currently in route"
                    )
                }
            }

            get("{id}/shortest-time") {
                val routeId = call.parameters["id"]?.toIntOrNull()
                    ?: throw BadRequestException("Invalid route ID")

                try {
                    val (shortestTime, fastestCarId) = service.getShortestTime(routeId)
                    val response =
                        mapOf("shortestTimeSec" to shortestTime, "fastestCarId" to fastestCarId)
                    call.respond(HttpStatusCode.OK, response)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError, mapOf(
                            "message" to "Error while fetching the fastest car data",
                            "details" to e.localizedMessage
                        )
                    )
                }
            }

            get("{id}") {
                val id =
                    call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                try {
                    val route = service.getRoutesById(id)
                        ?: throw NotFoundException("Route with ID $id not found")
                    call.respond(route)
                } catch (e: NotFoundException) {
                    throw e
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "An error occurred while fetching the route")
                    )
                }
            }

            post {
                try {
                    val routeDto = call.receive<RoutesDto>()
                    val addedId = service.addRoute(routeDto)
                    call.respond(
                        HttpStatusCode.Created,
                        mapOf("message" to "Route added successfully", "id" to addedId)
                    )
                } catch (e: Exception) {
                    println("Error while adding route: $e")
                    call.respond(HttpStatusCode.InternalServerError, "Error while adding route")
                }
            }

            put("{id}") {
                val id =
                    call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                try {
                    val routeDto = call.receive<RoutesDto>().copy(id = id)
                    val updated = service.updateRoute(routeDto)
                    if (updated) {
                        call.respond(
                            HttpStatusCode.OK, mapOf("message" to "Route updated successfully")
                        )
                    } else {
                        throw NotFoundException("Route with ID $id not found")
                    }
                } catch (e: SQLException) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "Database error occurred while updating route")
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "An unexpected error occurred while updating route")
                    )
                }
            }

            delete("{id}") {
                val id =
                    call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                try {
                    val deleted = service.deleteRoute(id)
                    if (deleted) {
                        call.respond(
                            HttpStatusCode.OK, mapOf("message" to "Route deleted successfully")
                        )
                    } else {
                        throw NotFoundException("Route with ID $id not found")
                    }
                } catch (e: SQLException) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "Database error occurred while deleting route")
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "An unexpected error occurred while deleting route")
                    )
                }
            }
        }
    }
}
