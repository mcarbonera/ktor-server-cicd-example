package com.evennt.application.auth.useCases

import com.evennt.application.auth.dto.SignOutRequestOutDto
import com.evennt.application.dtos.VerifyTokenResponseDto
import com.evennt.application.services.AuthVerificationService
import com.evennt.core.types.Outcome
import com.evennt.domain.repositories.TokenRevocationRepository
import com.evennt.domain.valueObjects.AuthTokenPayload
import java.util.*

class SignOutUseCase(
    private val tokenRevocationRepository: TokenRevocationRepository,
    private val verificationService: AuthVerificationService
) {
    enum class Reason {
        INVALID_TOKEN
    }

    suspend fun execute(requestDto: SignOutRequestOutDto): Outcome<Unit, Reason> {
        println("executando")
        val accessTokenResult = this.verificationService.validateAccessToken(requestDto.accessToken)
        val accessToken: AuthTokenPayload = when (accessTokenResult) {
            is VerifyTokenResponseDto.Error -> return Outcome.Failure(Reason.INVALID_TOKEN)
            is VerifyTokenResponseDto.Success -> accessTokenResult.payload
        }
        println("passou do access")
        val refreshTokenResult = this.verificationService.validateRefreshToken(requestDto.refreshToken)
        val refreshToken: AuthTokenPayload = when (refreshTokenResult) {
            is VerifyTokenResponseDto.Error -> return Outcome.Failure(Reason.INVALID_TOKEN)
            is VerifyTokenResponseDto.Success -> refreshTokenResult.payload
        }
        println("passou do refresh")

        revokeToken(refreshToken)
        revokeToken(accessToken)

        println("Tokens removidos")

        return Outcome.Success(Unit)
    }

    fun revokeToken(token: AuthTokenPayload) {
        val jti = token.jti
        tokenRevocationRepository.revokeToken(jti, Date(token.exp))

    }
}