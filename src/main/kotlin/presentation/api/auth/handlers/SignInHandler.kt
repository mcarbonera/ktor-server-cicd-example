package com.evennt.presentation.api.auth.handlers

import com.evennt.application.auth.dto.SignInRequestDto
import com.evennt.application.auth.dto.SignInResponseDto
import com.evennt.application.auth.useCases.SignInUseCase
import com.evennt.core.types.Outcome
import com.evennt.presentation.api.auth.schemas.SignInRequestSchema
import com.evennt.presentation.api.auth.schemas.SignInResponseSchema


class SignInHandler(private val useCase: SignInUseCase) {
    enum class Reason {
        INVALID_USER
    }

    suspend fun handle(requestBody: SignInRequestSchema): Outcome<SignInResponseSchema, Reason> {
        val signInRequestDto = SignInRequestDto(
            email = requestBody.email,
            hashedPassword = requestBody.password,
        )
        when (val result: SignInResponseDto = useCase.execute(signInRequestDto)) {
            is SignInResponseDto.Error -> return Outcome.Failure(
                Reason.INVALID_USER,
            )

            is SignInResponseDto.Success -> {
                val response = SignInResponseSchema(
                    accessToken = result.accessToken,
                    refreshToken = result.refreshToken,
                )
                return Outcome.Success(
                    value = response,
                )
            }
        }
    }
}