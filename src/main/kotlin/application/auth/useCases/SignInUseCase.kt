package com.evennt.application.auth.useCases

import com.evennt.application.auth.dto.SignInRequestDto
import com.evennt.application.auth.dto.SignInResponseDto
import com.evennt.application.services.AuthGenerationService
import com.evennt.domain.repositories.UserRepository
import com.evennt.domain.services.HashingService

class SignInUseCase(
    private val userRepository: UserRepository,
    private val hashingService: HashingService,
    private val authGenerationService: AuthGenerationService
) {

    suspend fun execute(requestDto: SignInRequestDto): SignInResponseDto {
        val user = userRepository.retrieveUserByEmail(requestDto.email)
            ?: return SignInResponseDto.Error(SignInResponseDto.Reason.USER_NOT_FOUND)
        val passwordEqual = hashingService.compare(requestDto.hashedPassword, user.password)
        if (!passwordEqual) {
            return SignInResponseDto.Error(SignInResponseDto.Reason.WRONG_PASSWORD)
        }
        val refreshToken: String = authGenerationService.createRefreshToken(user.id!!)
        val accessToken: String = authGenerationService.createAccessToken(user.id)

        return SignInResponseDto.Success(accessToken, refreshToken)
    }
}