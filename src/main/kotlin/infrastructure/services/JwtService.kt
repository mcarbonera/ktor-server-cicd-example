package com.evennt.infrastructure.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.exceptions.JWTVerificationException
import com.auth0.jwt.interfaces.DecodedJWT
import com.auth0.jwt.interfaces.Payload
import com.evennt.domain.services.AuthTokenService
import com.evennt.domain.valueObjects.AuthTokenPayload
import com.evennt.domain.valueObjects.TokenType
import java.util.*

class JwtService(
    private val issuer: String,
    private val audience: String,
) : AuthTokenService {

    override fun encode(payload: AuthTokenPayload, expirationInMillis: Long, secret: String): String {
        val algorithm = Algorithm.HMAC256(secret)
        val issuedAt = if (payload.iat > 0) {
            Date(payload.iat)
        } else {
            Date()
        }
        val expiresAt = if (payload.exp > 0) {
            Date(payload.exp)
        } else {
            Date(System.currentTimeMillis() + expirationInMillis)
        }

        return JWT.create()
            .withIssuer(issuer)
            .withAudience(audience)
            .withSubject(payload.sub)
            .withIssuedAt(issuedAt)
            .withExpiresAt(expiresAt)
            .withJWTId(payload.jti)
            .withClaim("type", payload.type.name)
            .sign(algorithm)
    }

    override fun decode(token: String, secret: String): AuthTokenPayload? {

        return try {
            val jwt = verifyToken(token, secret)

            val typeString = jwt.getClaim("type").asString()
            val tokenType = if (typeString != null) {
                try {
                    TokenType.valueOf(typeString)
                } catch (e: IllegalArgumentException) {
                    TokenType.ACCESS
                }
            } else {
                TokenType.ACCESS
            }

            AuthTokenPayload(
                sub = jwt.subject ?: "",
                iat = jwt.issuedAt?.time ?: 0L,
                exp = jwt.expiresAt?.time ?: 0L,
                jti = jwt.id ?: UUID.randomUUID().toString(),
                type = tokenType
            )
        } catch (e: JWTVerificationException) {
            null
        }
    }

    override fun decode(decodedJWT: Payload, secret: String): AuthTokenPayload? {
        return try {
            AuthTokenPayload(
                sub = decodedJWT.subject,
                exp = decodedJWT.expiresAt.toInstant().toEpochMilli(),
                iat = decodedJWT.issuedAt?.time ?: return null,
                jti = decodedJWT.id,
                type = TokenType.valueOf(decodedJWT.getClaim("type").asString()),
            )
        } catch (e: Exception) {
            return null
        }
    }

    private fun verifyToken(token: String, secret: String): DecodedJWT {
        val algorithm = Algorithm.HMAC256(secret)
        val verifier = JWT.require(algorithm)
            .withIssuer(issuer)
            .withAudience(audience)
            .build()

        val jwt = verifier.verify(token)
        return jwt
    }
}