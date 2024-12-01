package com.example.models

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.jodatime.datetime

object Journal : IntIdTable() {
    val timeOut = datetime("time_out")
    val timeIn = datetime("time_in")
    val routeId = integer("route_id").references(Routes.id)
    val autoId = integer("auto_id").references(Auto.id)
}
