package com.example.models

import org.jetbrains.exposed.dao.id.IntIdTable

object Auto : IntIdTable() {
    val num = varchar("num", 50)
    val color = varchar("color", 50)
    val mark = varchar("mark", 50)
    val personalId = integer("personal_id").references(AutoPersonnel.id)
}