package com.example.dto

import kotlinx.serialization.Serializable
import org.joda.time.DateTime
import java.text.SimpleDateFormat

@Serializable
data class JournalDto(
    val id: Int,
    val timeOut: String,
    val timeIn: String,
    val routeId: Int,
    val autoId: Int
) {
    fun stringToDateTimes(): Pair<DateTime, DateTime> {
        val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
        val parsedTimeOut = DateTime(format.parse(timeOut))
        val parsedTimeIn = DateTime(format.parse(timeIn))
        return Pair(parsedTimeOut, parsedTimeIn)
    }
}
