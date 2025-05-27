package com.evennt.presentation.api.auth.schemas

import kotlinx.serialization.Serializable

@Serializable
data class GetAuthUserResponseSchema(
    val id: String,
    val name: String,
    val email: String,
    val createdAt: String,
    val updatedAt: String
)