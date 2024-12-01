package com.example.dto

import kotlinx.serialization.Serializable

@Serializable
data class UsersDto(
    val id: Int,
    val username: String,
    val email: String,
    val password: String,
    val isAdmin: Boolean
)
