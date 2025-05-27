package com.evennt.presentation.api.auth.schemas

import kotlinx.serialization.Serializable

@Serializable
data class SignUpResponseSchema(
    val id: String,
    val auxiliarCode: String?,
    val email: String,
    val name: String,
    val accessToken: String,
    val refreshToken: String,
    val createdAt: String,
    val updatedAt: String
)