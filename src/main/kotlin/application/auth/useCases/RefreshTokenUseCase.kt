package com.evennt.application.auth.useCases

import com.evennt.application.auth.dto.RefreshTokenRequestDto
import com.evennt.application.auth.dto.RefreshTokenResponseDto
import com.evennt.application.dtos.VerifyTokenResponseDto
import com.evennt.application.services.AuthGenerationService
import com.evennt.application.services.AuthVerificationService
import com.evennt.core.types.Outcome
import com.evennt.core.types.Outcome.Failure
import com.evennt.domain.valueObjects.AuthTokenPayload

class RefreshTokenUseCase(
    private val verificationService: AuthVerificationService,
    private val generationService: AuthGenerationService,
) {
    enum class Reason {
        INVALID_TOKEN,
        EXPIRED_TOKEN,
        REVOKED,
        INVALID_USER,
    }

    suspend fun execute(requestDto: RefreshTokenRequestDto): Outcome<RefreshTokenResponseDto, Reason> {
        val tokenResponse: VerifyTokenResponseDto = verificationService.validateRefreshToken(requestDto.refreshToken)
        return when (tokenResponse) {
            is VerifyTokenResponseDto.Error -> tokenResponse.reason.toFailureOutcome()
            is VerifyTokenResponseDto.Success -> generateNewToken(tokenResponse.payload)
        }
    }

    private fun generateNewToken(payload: AuthTokenPayload): Outcome.Success<RefreshTokenResponseDto> {
        val newToken: String = generationService.createAccessToken(payload.sub)
        val responseDto = RefreshTokenResponseDto(newToken)
        return Outcome.Success(responseDto)
    }

    private fun VerifyTokenResponseDto.Reason.toFailureOutcome(): Failure<Reason> =
        when (this) {
            VerifyTokenResponseDto.Reason.EXPIRED -> Failure(Reason.EXPIRED_TOKEN)
            VerifyTokenResponseDto.Reason.USER_DOES_NOT_EXIST -> Failure(Reason.INVALID_TOKEN)
            VerifyTokenResponseDto.Reason.INVALID -> Failure(Reason.INVALID_TOKEN)
            VerifyTokenResponseDto.Reason.REVOKED -> Failure(Reason.REVOKED)
            VerifyTokenResponseDto.Reason.WRONG_TYPE -> Failure(Reason.INVALID_TOKEN)
            VerifyTokenResponseDto.Reason.USER_CANNOT_LOG_IN -> Failure(Reason.INVALID_USER)
        }
}
