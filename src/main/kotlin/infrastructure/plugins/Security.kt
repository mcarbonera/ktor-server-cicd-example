package com.evennt.infrastructure.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.evennt.application.dtos.VerifyTokenResponseDto
import com.evennt.application.services.AuthVerificationService
import com.evennt.core.Settings
import com.evennt.domain.services.AuthTokenService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.response.respond
import org.koin.ktor.ext.inject

fun Application.configureSecurity() {
    val jwtAudience = Settings.JWT_AUDIENCE
    val jwtIssuer = Settings.JWT_ISSUER
    val jwtRealm = Settings.JWT_REALM
    val jwtSecret = Settings.JWT_ACCESS_SECRET

    val tokenService: AuthTokenService by inject()
    val authVerificationService: AuthVerificationService by inject()

    install(Authentication) {
        jwt("auth-jwt") {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtIssuer)
                    .build()
            )
            validate { credential ->
                val token = credential.payload
                val payload = tokenService.decode(token)
                if (payload == null) {
                    return@validate null
                }
                val verifiedPayload = authVerificationService.validateAccessToken(payload)
                return@validate when (verifiedPayload) {
                    is VerifyTokenResponseDto.Error -> null
                    is VerifyTokenResponseDto.Success ->  JWTPrincipal(credential.payload)
                }
            }
            challenge { _, _ ->
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid or expired token"))
            }
        }
    }
}
