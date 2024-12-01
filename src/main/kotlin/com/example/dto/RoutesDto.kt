package com.example.dto

import kotlinx.serialization.Serializable

@Serializable
data class RoutesDto(
    val id: Int? = null,
    val name: String
)
