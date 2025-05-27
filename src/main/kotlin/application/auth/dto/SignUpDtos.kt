package com.evennt.application.auth.dto

import kotlinx.datetime.LocalDateTime

data class SignUpRequestDto(
    val email: String,
    val password: String,
    val name: String,
)

sealed class SignUpResponseDto {
    data class Success(
        val id: String,
        val auxiliarCode: String?,
        val email: String,
        val name: String,
        val accessToken: String,
        val refreshToken: String,
        val createdAt: LocalDateTime,
        val updatedAt: LocalDateTime,
    ) : SignUpResponseDto()

    data class Error(
        val reason: Reason,
    ) : SignUpResponseDto()

    enum class Reason {
        EMAIL_ALREADY_EXISTS,
        INTERNAL_ERROR,
    }
}
