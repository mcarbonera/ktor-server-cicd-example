package com.evennt.application.auth.dto

import kotlinx.datetime.LocalDateTime

data class GetAuthUserRequestDto(
    val sub: String
)

data class GetAuthUserResponseDto(
    val id: String,
    val email: String,
    val name: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)
