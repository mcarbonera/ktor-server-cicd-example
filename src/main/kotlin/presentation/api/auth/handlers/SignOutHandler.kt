package com.evennt.presentation.api.auth.handlers

import com.evennt.application.auth.dto.SignOutRequestOutDto
import com.evennt.application.auth.useCases.SignOutUseCase
import com.evennt.core.types.Outcome

class SignOutHandler(private val useCase: SignOutUseCase) {

    enum class Reason {
        INVALID_TOKEN
    }

    suspend fun handle(accessToken: String, refreshToken: String): Outcome<Unit, Reason> {
        val requestDto = SignOutRequestOutDto(accessToken = accessToken, refreshToken = refreshToken)
        val outcome = useCase.execute(requestDto)
        return when (outcome) {
            is Outcome.Failure<*> -> Outcome.Failure(Reason.INVALID_TOKEN)
            is Outcome.Success<*> -> Outcome.Success(Unit)
        }
    }
}