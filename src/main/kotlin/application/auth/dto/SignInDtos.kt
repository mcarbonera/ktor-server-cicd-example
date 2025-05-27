package com.evennt.application.auth.dto

data class SignInRequestDto(
    val email: String,
    val hashedPassword: String
)

sealed class SignInResponseDto {

    data class Success(
        val accessToken: String,
        val refreshToken: String) : SignInResponseDto()
    data class Error(val reason: Reason) : SignInResponseDto()

    enum class Reason {
        USER_NOT_FOUND,
        WRONG_PASSWORD,
    }

}