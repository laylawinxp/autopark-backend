package com.example.services

import com.example.configDb.DataBaseFactory.dbQuery
import com.example.models.Auto
import com.example.dto.AutoDto
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

class AutoService {
    suspend fun getAllAutos(): List<AutoDto> {
        return dbQuery {
            Auto.selectAll().map(::toAutoDto)
        }
    }

    suspend fun getAutoById(id: Int): AutoDto? {
        return dbQuery {
            Auto.select { Auto.id eq id }.map(::toAutoDto).singleOrNull()
        }
    }

    suspend fun addAuto(autoDto: AutoDto): Int {
        return dbQuery {
            Auto.insertAndGetId {
                it[num] = autoDto.num
                it[color] = autoDto.color
                it[mark] = autoDto.mark
                it[personalId] = autoDto.personalId
            }.value
        }
    }

    suspend fun updateAuto(autoDto: AutoDto): Boolean {
        return dbQuery {
            val count = Auto.update({ Auto.id eq autoDto.id }) {
                it[num] = autoDto.num
                it[color] = autoDto.color
                it[mark] = autoDto.mark
                it[personalId] = autoDto.personalId
            }
            count > 0
        }
    }

    suspend fun deleteAuto(id: Int): Boolean {
        return dbQuery {
            val count = Auto.deleteWhere { Auto.id eq id }
            count > 0
        }
    }

    private fun toAutoDto(row: ResultRow): AutoDto {
        return AutoDto(
            id = row[Auto.id].value,
            num = row[Auto.num],
            color = row[Auto.color],
            mark = row[Auto.mark],
            personalId = row[Auto.personalId]
        )
    }
}