package com.example.services

import com.example.configDb.DataBaseFactory.dbQuery
import com.example.controllers.TriggerException
import com.example.dto.AutoPersonnelDto
import com.example.models.AutoPersonnel
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import java.sql.SQLException

class AutoPersonnelService {
    suspend fun getAllPersonnel(): List<AutoPersonnelDto> {
        return dbQuery {
            AutoPersonnel.selectAll().map(::toAutoPersonnelDto)
        }
    }

    suspend fun getPersonnelById(id: Int): AutoPersonnelDto? {
        return dbQuery {
            AutoPersonnel.select { AutoPersonnel.id eq id }.map(::toAutoPersonnelDto).singleOrNull()
        }
    }

    suspend fun addPersonnel(personnelDto: AutoPersonnelDto): Int {
        return dbQuery {
            AutoPersonnel.insertAndGetId {
                it[firstName] = personnelDto.firstName
                it[lastName] = personnelDto.lastName
                it[fatherName] = personnelDto.fatherName
            }.value
        }
    }

    suspend fun updatePersonnel(personnelDto: AutoPersonnelDto): Boolean {
        return dbQuery {
            val count = AutoPersonnel.update({ AutoPersonnel.id eq personnelDto.id }) {
                it[firstName] = personnelDto.firstName
                it[lastName] = personnelDto.lastName
                it[fatherName] = personnelDto.fatherName
            }
            count > 0
        }
    }

    suspend fun deletePersonnel(id: Int): Boolean {
        return try {
            dbQuery {
                val count = AutoPersonnel.deleteWhere { AutoPersonnel.id eq id }
                count > 0
            }
        } catch (e: SQLException) {
            if (e.message?.contains("Trigger") == true) {
                throw TriggerException("Trigger error: you cannot delete personnel with refs", e)
            } else {
                throw e
            }
        }
    }

    private fun toAutoPersonnelDto(row: ResultRow): AutoPersonnelDto {
        return AutoPersonnelDto(
            id = row[AutoPersonnel.id].value,
            firstName = row[AutoPersonnel.firstName],
            lastName = row[AutoPersonnel.lastName],
            fatherName = row[AutoPersonnel.fatherName]
        )
    }
}