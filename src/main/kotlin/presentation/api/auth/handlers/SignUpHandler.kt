package com.evennt.presentation.api.auth.handlers

import com.evennt.application.auth.dto.SignUpRequestDto
import com.evennt.application.auth.dto.SignUpResponseDto
import com.evennt.application.auth.useCases.SignUpUseCase
import com.evennt.presentation.api.auth.schemas.SignUpRequestSchema
import com.evennt.presentation.api.auth.schemas.SignUpResponseSchema
import io.ktor.http.HttpStatusCode

class SignUpHandler(private val useCase: SignUpUseCase) {

    data class SignUpResult(
        val statusCode: HttpStatusCode,
        val data: Any
    )

    suspend fun handle(requestBody: SignUpRequestSchema): SignUpResult {
        val signUpRequestDto = SignUpRequestDto(
            email = requestBody.email,
            password = requestBody.password,
            name = requestBody.name
        )

        when (val result: SignUpResponseDto = useCase.execute(signUpRequestDto)) {
            is SignUpResponseDto.Error -> {
                val status = when (result.reason) {
                    SignUpResponseDto.Reason.EMAIL_ALREADY_EXISTS -> HttpStatusCode.Conflict
                    SignUpResponseDto.Reason.INTERNAL_ERROR -> HttpStatusCode.InternalServerError
                }
                return SignUpResult(status, result)
            }

            is SignUpResponseDto.Success -> {
                val response = SignUpResponseSchema(
                    id = result.id,
                    auxiliarCode = result.auxiliarCode,
                    email = result.email,
                    name = result.name,
                    accessToken = result.accessToken,
                    refreshToken = result.refreshToken,
                    createdAt = result.createdAt.toString(),
                    updatedAt = result.updatedAt.toString()
                )
                return SignUpResult(HttpStatusCode.Created, response)
            }
        }
    }
}