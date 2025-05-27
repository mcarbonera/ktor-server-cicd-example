package com.evennt.infrastructure.persistence.memory

import com.evennt.domain.repositories.TokenRevocationRepository
import java.util.Date

class TokenRevocationMemoryRepository: TokenRevocationRepository {
    private data class RevokedToken(val jti: String, val expiration: Date)
    private val revokedTokens = mutableListOf<RevokedToken>()

    override fun revokeToken(jti: String, expiration: Date) {
        revokedTokens.add(RevokedToken(jti, expiration))
    }

    override fun verifyTokenRevoked(jti: String): Boolean {
        val token = revokedTokens.find { it.jti == jti }
        return token != null
    }
}