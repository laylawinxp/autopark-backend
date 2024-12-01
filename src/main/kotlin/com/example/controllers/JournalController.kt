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

class JournalController(private val service: JournalService) {
    fun setupRoutes(routing: Routing) {
        routing.route("/journal") {
            get {
                val journals = service.getAllJournals()
                call.respond(journals)
            }

            get("{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                val journal = service.getJournalById(id) ?: throw NotFoundException("Journal with ID $id not found")
                call.respond(journal)
            }

            post {
                val journalDto = call.receive<JournalDto>()
                val addedId = service.addJournal(journalDto)
                call.respond(HttpStatusCode.Created, mapOf("message" to "Journal added successfully", "id" to addedId))
            }

            put("{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                val journalDto = call.receive<JournalDto>().copy(id = id)
                val updated = service.updateJournal(journalDto)
                if (updated) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Journal updated successfully"))
                } else {
                    throw NotFoundException("Journal with ID $id not found")
                }
            }

            delete("{id}") {
                val id = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException("Invalid ID")
                val deleted = service.deleteJournal(id)
                if (deleted) {
                    call.respond(HttpStatusCode.OK, mapOf("message" to "Journal deleted successfully"))
                } else {
                    throw NotFoundException("Journal with ID $id not found")
                }
            }
        }
    }
}