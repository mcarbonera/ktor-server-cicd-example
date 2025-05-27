package com.evennt.application.dtos

import com.evennt.domain.valueObjects.AuthTokenPayload
import kotlinx.serialization.Serializable

sealed class VerifyTokenResponseDto {
    data class Success(val payload: AuthTokenPayload): VerifyTokenResponseDto()
    data class Error(val reason: Reason): VerifyTokenResponseDto()

    @Serializable
    enum class Reason {
        EXPIRED,
        USER_DOES_NOT_EXIST,
        INVALID,
        REVOKED,
        WRONG_TYPE,
        USER_CANNOT_LOG_IN
    }
}
