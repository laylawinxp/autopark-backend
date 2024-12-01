package com.example.controllers

import com.example.dto.UsersDto
import com.example.services.UsersService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Routing
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.put
import io.ktor.server.routing.route

class UserController(private val userService: UsersService) {

    fun setupRoutes(routing: Routing) {
        routing.route("/users") {
            post {
                val usersDto = call.receive<UsersDto>()
                try {
                    val userId = userService.addUser(usersDto)
                    call.respond(HttpStatusCode.Created, mapOf("message" to "User created successfully", "id" to userId))
                } catch (e: Exception) {
                    call.respond(HttpStatusCode.InternalServerError, "Error creating user")
                }
            }

            get {
                val users = userService.getAllUsers()
                call.respond(users)
            }

            get("{id}") {
                val userId = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                val user = userService.getUserById(userId) ?: throw NotFoundException("User with ID $userId not found")
                call.respond(user)
            }


            put("{id}") {
                val userId = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                val usersDto = call.receive<UsersDto>().copy(id = userId)
                if (userService.updateUser(usersDto)) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "User updated successfully"))
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            }

            delete("{id}") {
                val userId = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                if (userService.deleteUser(userId)) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "User deleted successfully"))
                } else {
                    call.respond(HttpStatusCode.NotFound, "User not found")
                }
            }

        }
    }
}
