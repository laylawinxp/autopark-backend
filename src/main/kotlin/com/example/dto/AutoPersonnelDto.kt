package com.example.dto

import kotlinx.serialization.Serializable

@Serializable
data class AutoPersonnelDto(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val fatherName: String
)