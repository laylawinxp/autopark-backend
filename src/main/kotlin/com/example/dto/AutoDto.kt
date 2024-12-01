package com.example.dto

import kotlinx.serialization.Serializable

@Serializable
data class AutoDto(
    val id: Int,
    val num: String,
    val color: String,
    val mark: String,
    val personalId: Int
)