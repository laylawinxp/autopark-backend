package com.example.models

import org.jetbrains.exposed.dao.id.IntIdTable

object Users : IntIdTable() {
    val username = varchar("username", 50).uniqueIndex()
    val email = varchar("email", 100).uniqueIndex()
    val password = text("password")
    val isAdmin = bool("isadmin").default(false)
}
