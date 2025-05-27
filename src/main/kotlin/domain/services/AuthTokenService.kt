package com.evennt.domain.services

import com.auth0.jwt.interfaces.Payload
import com.evennt.core.Settings
import com.evennt.domain.valueObjects.AuthTokenPayload

interface AuthTokenService {
    fun encode(payload: AuthTokenPayload, expirationInMillis: Long, secret: String = Settings.JWT_ACCESS_SECRET): String
    fun decode(token: String, secret: String = Settings.JWT_REFRESH_SECRET): AuthTokenPayload?
    fun decode(decodedJWT: Payload, secret: String = Settings.JWT_REFRESH_SECRET): AuthTokenPayload?
}