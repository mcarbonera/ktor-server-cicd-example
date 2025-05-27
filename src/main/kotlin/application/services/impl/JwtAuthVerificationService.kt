package com.evennt.application.services.impl

import com.evennt.application.dtos.VerifyTokenResponseDto
import com.evennt.application.services.AuthVerificationService
import com.evennt.core.Settings
import com.evennt.domain.repositories.TokenRevocationRepository
import com.evennt.domain.repositories.UserRepository
import com.evennt.domain.services.AuthTokenService
import com.evennt.domain.valueObjects.AuthTokenPayload
import com.evennt.domain.valueObjects.TokenType

class JwtAuthVerificationService(
    val authTokenService: AuthTokenService,
    val userRepository: UserRepository,
    val tokenRevocationRepository: TokenRevocationRepository,
) : AuthVerificationService {
    override suspend fun validateAccessToken(token: String): VerifyTokenResponseDto {
        return validateToken(
            token, TokenType.ACCESS, Settings.JWT_ACCESS_SECRET
        )
    }

    override suspend fun validateRefreshToken(token: String): VerifyTokenResponseDto {
        return validateToken(
            token, TokenType.REFRESH, Settings.JWT_REFRESH_SECRET
        )
    }

    override suspend fun validateAccessToken(payload: AuthTokenPayload): VerifyTokenResponseDto {
        return validateTokenPayload(payload, TokenType.ACCESS)
    }

    private suspend fun validateToken(
        token: String, type: TokenType, secret: String
    ): VerifyTokenResponseDto {
        val payload = try {
            authTokenService.decode(token, secret)
        } catch (e: Exception) {
            return VerifyTokenResponseDto.Error(VerifyTokenResponseDto.Reason.INVALID)
        }

        if (payload == null) {
            return VerifyTokenResponseDto.Error(VerifyTokenResponseDto.Reason.INVALID)
        }

        return validateTokenPayload(payload, type)
    }

    private suspend fun validateTokenPayload(
        payload: AuthTokenPayload, type: TokenType
    ): VerifyTokenResponseDto {
        if (!payload.isValidType(type)) {
            return VerifyTokenResponseDto.Error(VerifyTokenResponseDto.Reason.WRONG_TYPE)
        }

        if (payload.isExpired()) {
            return VerifyTokenResponseDto.Error(VerifyTokenResponseDto.Reason.EXPIRED)
        }

        if (tokenRevocationRepository.verifyTokenRevoked(payload.jti)) {
            return VerifyTokenResponseDto.Error(VerifyTokenResponseDto.Reason.REVOKED)
        }

        val user = userRepository.retrieveUser(payload.sub)
        if (user == null) {
            return VerifyTokenResponseDto.Error(VerifyTokenResponseDto.Reason.USER_DOES_NOT_EXIST)
        }

        if (!user.canLogin()) {
            return VerifyTokenResponseDto.Error(
                VerifyTokenResponseDto.Reason.USER_CANNOT_LOG_IN
            )
        }

        if (!user.canUseToken(payload)) {
            return VerifyTokenResponseDto.Error(VerifyTokenResponseDto.Reason.INVALID)
        }
        return VerifyTokenResponseDto.Success(payload)
    }
}