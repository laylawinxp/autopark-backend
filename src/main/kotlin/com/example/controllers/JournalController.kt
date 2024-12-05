package com.example.controllers

import com.example.dto.JournalDto
import com.example.services.JournalService
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

class JournalController(private val service: JournalService) {
    fun setupRoutes(routing: Routing) {
        routing.route("/journal") {
            get {
                try {
                    val journals = service.getAllJournals()
                    call.respond(journals)
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "An error occurred while fetching journals")
                    )
                }
            }

            get("{id}") {
                val id =
                    call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                try {
                    val journal = service.getJournalById(id)
                        ?: throw NotFoundException("Journal with ID $id not found")
                    call.respond(journal)
                } catch (e: NotFoundException) {
                    throw e
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "An error occurred while fetching the journal")
                    )
                }
            }

            post {
                val journalDto = try {
                    call.receive<JournalDto>()
                } catch (e: Exception) {
                    throw BadRequestException("Invalid journal data")
                }

                try {
                    val addedId = service.addJournal(journalDto)
                    call.respond(
                        HttpStatusCode.Created,
                        mapOf("message" to "Journal added successfully", "id" to addedId)
                    )
                } catch (e: TriggerException) {
                    call.respond(
                        HttpStatusCode.Conflict,
                        mapOf("message" to e.message)
                    )
                } catch (e: SQLException) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "Database error occurred while adding the journal")
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "An unexpected error occurred while adding the journal")
                    )
                }
            }

            put("{id}") {
                val id =
                    call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                val journalDto = try {
                    call.receive<JournalDto>().copy(id = id)
                } catch (e: Exception) {
                    throw BadRequestException("Invalid journal data")
                }

                try {
                    val updated = service.updateJournal(journalDto)
                    if (updated) {
                        call.respond(
                            HttpStatusCode.OK,
                            mapOf("message" to "Journal updated successfully")
                        )
                    } else {
                        throw NotFoundException("Journal with ID $id not found")
                    }
                } catch (e: TriggerException) {
                    call.respond(
                        HttpStatusCode.Conflict,
                        mapOf("message" to e.message)
                    )
                } catch (e: SQLException) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "Database error occurred while updating the journal")
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "An unexpected error occurred while updating the journal")
                    )
                }
            }

            delete("{id}") {
                val id =
                    call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                try {
                    val deleted = service.deleteJournal(id)
                    if (deleted) {
                        call.respond(
                            HttpStatusCode.OK,
                            mapOf("message" to "Journal deleted successfully")
                        )
                    } else {
                        throw NotFoundException("Journal with ID $id not found")
                    }
                } catch (e: SQLException) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "Database error occurred while deleting the journal")
                    )
                } catch (e: Exception) {
                    call.respond(
                        HttpStatusCode.InternalServerError,
                        mapOf("message" to "An unexpected error occurred while deleting the journal")
                    )
                }
            }
        }
    }
}
