package com.example.services

import com.example.configDb.DataBaseFactory.dbQuery
import com.example.dto.UsersDto
import com.example.models.Users
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.mindrot.jbcrypt.BCrypt

class UsersService {

    suspend fun getAllUsers(): List<UsersDto> {
        return dbQuery {
            Users.selectAll().map(::toUserDto)
        }
    }

    suspend fun getUserById(id: Int): UsersDto? {
        return dbQuery {
            Users.select { Users.id eq id }.map(::toUserDto).singleOrNull()
        }
    }

    suspend fun addUser(userDto: UsersDto): Int {
        val hashedPassword = hashPassword(userDto.password)
        return dbQuery {
            Users.insertAndGetId {
                it[username] = userDto.username
                it[email] = userDto.email
                it[password] = hashedPassword
                it[isAdmin] = userDto.isAdmin
            }.value
        }
    }

    suspend fun updateUser(userDto: UsersDto): Boolean {
        val hashedPassword = hashPassword(userDto.password)
        return dbQuery {
            val count = Users.update({ Users.id eq userDto.id }) {
                it[username] = userDto.username
                it[email] = userDto.email
                it[password] = hashedPassword
                it[isAdmin] = userDto.isAdmin
            }
            count > 0
        }
    }

    suspend fun deleteUser(id: Int): Boolean {
        return dbQuery {
            val count = Users.deleteWhere { Users.id eq id }
            count > 0
        }
    }

    private fun hashPassword(password: String): String {
        return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    suspend fun checkPassword(userId: Int, password: String): Boolean {
        val user = getUserById(userId) ?: return false
        val storedPassword = user.password
        return BCrypt.checkpw(password, storedPassword)
    }

    private fun toUserDto(row: ResultRow): UsersDto {
        return UsersDto(
            id = row[Users.id].value,
            username = row[Users.username],
            email = row[Users.email],
            password = row[Users.password],
            isAdmin = row[Users.isAdmin]
        )
    }
}
