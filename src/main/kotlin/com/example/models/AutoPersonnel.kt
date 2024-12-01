package com.example.models

import org.jetbrains.exposed.dao.id.IntIdTable

object AutoPersonnel : IntIdTable("auto_personnel") {
    val firstName = varchar("first_name", 100)
    val lastName = varchar("last_name", 100)
    val fatherName = varchar("father_name", 100)
}