package com.evennt.presentation.api.schemas

import kotlinx.serialization.Serializable

@Serializable
data class ListUsersResponseSchema(
    val id: String,
    val email: String,
    val createdAt: String,
    val updatedAt: String,
)