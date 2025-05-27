package com.evennt.domain.valueObjects

enum class TokenType {
    ACCESS,
    REFRESH,
}

data class AuthTokenPayload(
    val sub: String,
    val iat: Long,
    val exp: Long,
    val jti: String,
    val type: TokenType,
) {
    fun isExpired(): Boolean = exp < System.currentTimeMillis()

    fun isValidType(tokenType: TokenType): Boolean = type == tokenType
}
