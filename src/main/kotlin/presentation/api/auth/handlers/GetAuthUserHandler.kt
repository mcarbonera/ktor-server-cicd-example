package com.evennt.presentation.api.auth.handlers

import com.evennt.application.auth.dto.GetAuthUserRequestDto
import com.evennt.application.auth.useCases.GetAuthUserUseCase
import com.evennt.core.types.Outcome
import com.evennt.presentation.api.auth.schemas.GetAuthUserResponseSchema
import kotlinx.serialization.Serializable

class GetAuthUserHandler(private val useCase: GetAuthUserUseCase) {

    @Serializable
    enum class Reason {
        USER_NOT_FOUND
    }

    suspend fun handle(tokenSub: String): Outcome<GetAuthUserResponseSchema, Reason> {
        val requestDto = GetAuthUserRequestDto(
            sub = tokenSub
        )
        val response = useCase.execute(requestDto)
        return when(response) {
            is Outcome.Failure -> Outcome.Failure(Reason.USER_NOT_FOUND)
            is Outcome.Success -> {
                val userDto = response.value
                val responseSchema = GetAuthUserResponseSchema(
                    id = userDto.id,
                    name = userDto.name,
                    email = userDto.email,
                    createdAt = userDto.createdAt.toString(),
                    updatedAt = userDto.updatedAt.toString()
                )
                return Outcome.Success(responseSchema)
            }
        }
    }
}