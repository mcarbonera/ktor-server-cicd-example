package com.evennt.presentation.api.auth

import com.evennt.core.types.Outcome
import com.evennt.presentation.api.auth.handlers.*
import com.evennt.presentation.api.auth.schemas.*
import io.github.smiley4.ktoropenapi.resources.*
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import org.koin.ktor.ext.inject

@Serializable
@Resource("/auth")
class Auth {
    @Serializable
    @Resource("sign-up")
    class SignUp(val parent: Auth = Auth())

    @Serializable
    @Resource("sign-in")
    class SignIn(val parent: Auth = Auth())

    @Serializable
    @Resource("sign-out")
    class SignOut(val parent: Auth = Auth())

    @Serializable
    @Resource("me")
    class Me(val parent: Auth = Auth())
}

fun Application.registerAuthRoutes() {

    val signUpHandler: SignUpHandler by inject()
    val signInHandler: SignInHandler by inject()
    val signOutHandler: SignOutHandler by inject()
    val getAuthUserHandler: GetAuthUserHandler by inject()

    routing {
        post<Auth.SignUp>(
            {
                tags = listOf("auth")
                request { body<SignUpRequestSchema> { } }
                response { HttpStatusCode.OK to { body<SignUpResponseSchema> { } } }
            }) {
            val request = call.receive<SignUpRequestSchema>()
            val response = signUpHandler.handle(request)
            if (response.data is SignUpResponseSchema) {
                val refreshTokenCookie = Cookie(
                    name = "refreshToken",
                    value = response.data.refreshToken,
                    path = "/",
                    secure = false,
                    httpOnly = true,
                )
                call.response.cookies.append(refreshTokenCookie)
            }
            call.respond(response.statusCode, response.data)
        }

        post<Auth.SignIn>(
            {
                tags = listOf("auth")
                request { body<SignInRequestSchema> { } }
                response { HttpStatusCode.Created to { body<SignInResponseSchema> { } } }
                response { HttpStatusCode.Unauthorized to {} }
            }) {
            val request = call.receive<SignInRequestSchema>()
            when (val response = signInHandler.handle(request)) {
                is Outcome.Failure -> call.respond(HttpStatusCode.Unauthorized)
                is Outcome.Success -> {
                    val refreshTokenCookie = Cookie(
                        name = "refreshToken",
                        value = response.value.refreshToken,
                        path = "/",
                        secure = false,
                        httpOnly = true,
                    )
                    call.response.cookies.append(refreshTokenCookie)
                    call.respond(HttpStatusCode.Created, response.value)
                }
            }
        }

        authenticate("auth-jwt") {
            delete<Auth.SignOut>(
                {
                    tags = listOf("auth")
                    response { HttpStatusCode.NoContent to {} }
                    response { HttpStatusCode.Unauthorized to {} }
                }) {
                val accessToken =
                    call.request.authorization()?.removePrefix("Bearer ")
                        ?.trim()
                val refreshToken = call.request.cookies["refreshToken"]
                if (accessToken == null || refreshToken == null) {
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to "Missing Tokens")
                    )
                    return@delete
                }
                when (val result =
                    signOutHandler.handle(accessToken, refreshToken)) {
                    is Outcome.Success -> {
                        call.response.cookies.append(
                            Cookie(
                                name = "refreshToken",
                                value = "",
                                maxAge = 0,
                                path = "/",
                                httpOnly = true
                            )
                        )
                        call.respond(HttpStatusCode.OK)
                    }

                    is Outcome.Failure -> {
                        val reason = result.reason
                        call.respond(
                            HttpStatusCode.Unauthorized,
                            mapOf("error" to reason.name)
                        )
                    }
                }
            }

            get<Auth.Me>(
                {
                    tags = listOf("auth")
                    response { HttpStatusCode.OK to { body<GetAuthUserResponseSchema> { } } }
                    response { HttpStatusCode.Unauthorized to {} }
                }) {
                val tokenSub: String? =
                    call.principal<JWTPrincipal>()?.payload?.subject
                if (tokenSub == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@get
                }
                val response = getAuthUserHandler.handle(tokenSub)
                when (response) {
                    is Outcome.Failure -> call.respond(HttpStatusCode.NotFound)
                    is Outcome.Success -> call.respond(
                        HttpStatusCode.OK, response.value
                    )
                }
            }
        }
    }
}