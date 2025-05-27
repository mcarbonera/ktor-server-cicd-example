package com.evennt.application.services

import com.evennt.application.dtos.VerifyTokenResponseDto
import com.evennt.domain.valueObjects.AuthTokenPayload

interface AuthVerificationService {
    suspend fun validateAccessToken(token: String): VerifyTokenResponseDto

    suspend fun validateRefreshToken(token: String): VerifyTokenResponseDto

    suspend fun validateAccessToken(payload: AuthTokenPayload): VerifyTokenResponseDto
}
