package com.evennt.application.services.impl

import com.evennt.application.services.AuthGenerationService
import com.evennt.core.Settings
import com.evennt.domain.services.AuthTokenService
import com.evennt.domain.valueObjects.AuthTokenPayload
import com.evennt.domain.valueObjects.TokenType
import java.util.UUID

class JwtAuthGenerationService(private val tokenService: AuthTokenService) : AuthGenerationService {
    override fun createAccessToken(sub: String): String {
        val now = System.currentTimeMillis()
        val payload = AuthTokenPayload(
            sub = sub,
            iat = now,
            exp = now + Settings.JWT_ACCESS_EXPIRATION,
            jti = UUID.randomUUID().toString(),
            type = TokenType.ACCESS
        )
        return tokenService.encode(payload, payload.exp - payload.iat, Settings.JWT_ACCESS_SECRET)
    }

    override fun createRefreshToken(sub: String): String {
        val now = System.currentTimeMillis()
        val payload = AuthTokenPayload(
            sub = sub,
            iat = now,
            exp = now + Settings.JWT_REFRESH_EXPIRATION,
            jti = UUID.randomUUID().toString(),
            type = TokenType.REFRESH
        )
        return tokenService.encode(payload, payload.exp - payload.iat, Settings.JWT_REFRESH_SECRET)
    }
}