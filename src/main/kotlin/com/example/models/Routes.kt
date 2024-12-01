package com.example.models

import org.jetbrains.exposed.dao.id.IntIdTable

object Routes : IntIdTable() {
    val name = varchar("name", 100)
}