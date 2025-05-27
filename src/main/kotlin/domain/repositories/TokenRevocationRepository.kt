package com.evennt.domain.repositories

import java.util.Date

interface TokenRevocationRepository {
    fun revokeToken(jti: String, expiration: Date)
    fun verifyTokenRevoked(jti: String): Boolean
}