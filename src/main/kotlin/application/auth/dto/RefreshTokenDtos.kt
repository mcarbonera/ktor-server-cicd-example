package com.evennt.application.auth.dto

data class RefreshTokenRequestDto(val refreshToken: String)

data class RefreshTokenResponseDto(val accessToken: String)