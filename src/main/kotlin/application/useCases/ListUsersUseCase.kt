package com.evennt.application.useCases

import com.evennt.application.dtos.ListUsersResponseDto
import com.evennt.domain.repositories.UserRepository

class ListUsersUseCase(private val repository: UserRepository) {

    suspend fun execute(): List<ListUsersResponseDto> {
        val userEntities = repository.listUsers()
        return userEntities.map {
            ListUsersResponseDto(
                id = it.id!!,
                email = it.email.value,
                createdAt = it.createdAt,
                updatedAt = it.updatedAt
            )
        }
    }
}