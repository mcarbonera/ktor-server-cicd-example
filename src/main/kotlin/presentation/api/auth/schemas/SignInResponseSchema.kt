package com.evennt.presentation.api.auth.schemas

import kotlinx.serialization.Serializable

@Serializable
data class SignInResponseSchema(
    val accessToken: String,
    val refreshToken: String
)