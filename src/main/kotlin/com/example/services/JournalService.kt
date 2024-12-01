package com.example.services

import com.example.configDb.DataBaseFactory.dbQuery
import com.example.dto.JournalDto
import com.example.models.Journal
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class JournalService {

    suspend fun getAllJournals(): List<JournalDto> {
        return dbQuery {
            Journal.selectAll().map(::toJournalDto)
        }
    }

    suspend fun getJournalById(id: Int): JournalDto? {
        return dbQuery {
            Journal.select { Journal.id eq id }.map(::toJournalDto).singleOrNull()
        }
    }

    suspend fun addJournal(journalDto: JournalDto): Int {
        val (parsedTimeOut, parsedTimeIn) = journalDto.stringToDateTimes()
        return dbQuery {
            Journal.insertAndGetId {
                it[timeOut] = parsedTimeOut
                it[timeIn] = parsedTimeIn
                it[routeId] = journalDto.routeId
                it[autoId] = journalDto.autoId
            }.value
        }
    }

    suspend fun updateJournal(journalDto: JournalDto): Boolean {
        val (parsedTimeOut, parsedTimeIn) = journalDto.stringToDateTimes()
        return dbQuery {
            val count = Journal.update({ Journal.id eq journalDto.id }) {
                it[timeOut] = parsedTimeOut
                it[timeIn] = parsedTimeIn
                it[routeId] = journalDto.routeId
                it[autoId] = journalDto.autoId
            }
            count > 0
        }
    }

    suspend fun deleteJournal(id: Int): Boolean {
        return dbQuery {
            val count = Journal.deleteWhere { Journal.id eq id }
            count > 0
        }
    }

    private fun toJournalDto(row: ResultRow): JournalDto {
        return JournalDto(
            id = row[Journal.id].value,
            timeOut = row[Journal.timeOut]?.toString() ?: "-",
            timeIn = row[Journal.timeIn]?.toString() ?: "-",
            routeId = row[Journal.routeId],
            autoId = row[Journal.autoId]
        )
    }
}