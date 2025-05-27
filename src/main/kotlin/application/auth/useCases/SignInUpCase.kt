package com.evennt.application.auth.useCases

import com.evennt.application.auth.dto.SignUpRequestDto
import com.evennt.application.auth.dto.SignUpResponseDto
import com.evennt.application.services.AuthGenerationService
import com.evennt.domain.entities.User
import com.evennt.domain.repositories.UserRepository
import com.evennt.domain.services.HashingService
import com.evennt.domain.valueObjects.Email
import kotlin.random.Random

class SignUpUseCase(
    private val userRepository: UserRepository,
    private val hashingService: HashingService,
    private val authGenerationService: AuthGenerationService
) {

    suspend fun execute(requestDto: SignUpRequestDto): SignUpResponseDto {
        if (verifyUserWithEmailExists(requestDto.email)) {
            return SignUpResponseDto.Error(SignUpResponseDto.Reason.EMAIL_ALREADY_EXISTS)
        }
        val newUser = dto2domain(requestDto)
        val savedUser = saveUser(newUser)

        return SignUpResponseDto.Success(
            id = savedUser.id!!,
            auxiliarCode = savedUser.auxiliarCode,
            email = savedUser.email.value,
            name = savedUser.name,
            accessToken = authGenerationService.createAccessToken(savedUser.id),
            refreshToken = authGenerationService.createRefreshToken(savedUser.id),
            createdAt = savedUser.createdAt,
            updatedAt = savedUser.updatedAt
        )
    }

    private fun dto2domain(requestDto: SignUpRequestDto): User {
        val passwordHash = hashingService.hash(requestDto.password)
        val newUser = User(
            auxiliarCode = Random.nextInt().toString(),
            email = Email(requestDto.email),
            password = passwordHash,
            name = requestDto.name,
        )
        return newUser
    }

    private suspend fun verifyUserWithEmailExists(email: String): Boolean {
        val existingUser = userRepository.retrieveUserByEmail(email)
        return existingUser != null
    }

    private suspend fun saveUser(user: User): User {
        return  userRepository.createUser(user)
    }

}