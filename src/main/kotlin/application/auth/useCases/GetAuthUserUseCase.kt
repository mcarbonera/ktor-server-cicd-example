package com.evennt.application.auth.useCases

import com.evennt.application.auth.dto.GetAuthUserRequestDto
import com.evennt.application.auth.dto.GetAuthUserResponseDto
import com.evennt.core.types.Outcome
import com.evennt.domain.repositories.UserRepository

class GetAuthUserUseCase(val userRepository: UserRepository) {

    enum class Reason {
        USER_NOT_FOUND
    }

    suspend fun execute(requestDto: GetAuthUserRequestDto): Outcome<GetAuthUserResponseDto, Reason> {
        val user = userRepository.retrieveUser(requestDto.sub)
        if (user == null) {
            return Outcome.Failure(Reason.USER_NOT_FOUND)
        }
        val response = GetAuthUserResponseDto(
            id = user.id!!,
            email = user.email.value,
            name = user.name,
            createdAt = user.createdAt,
            updatedAt = user.updatedAt
        )
        return Outcome.Success(response)
    }

}