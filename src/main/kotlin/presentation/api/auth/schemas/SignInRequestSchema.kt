package com.evennt.presentation.api.auth.schemas

import kotlinx.serialization.Serializable

@Serializable
data class SignInRequestSchema(
    val email: String,
    val password: String
)