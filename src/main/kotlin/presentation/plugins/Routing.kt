package com.evennt.presentation.plugins

import com.evennt.presentation.api.auth.registerAuthRoutes
import com.evennt.presentation.api.routes.registerUserRoutes
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.requestvalidation.RequestValidationException
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*

fun Application.configureRouting() {
    install(StatusPages) {
        exception<RequestValidationException> { call, cause ->
            val errorMessages = cause.reasons.joinToString("; ")
            call.respond(
                status = HttpStatusCode.BadRequest,
                message = mapOf("error" to "Validation failed", "details" to errorMessages)
            )
        }

        exception<BadRequestException> { call, cause ->
            // Get the root cause which might be a ConstraintViolationException
            var rootCause: Throwable? = cause
            while (rootCause?.cause != null && rootCause.cause != rootCause) {
                rootCause = rootCause.cause
            }

            when (rootCause) {
                is org.valiktor.ConstraintViolationException -> {
                    // Format validation errors nicely
                    val errors = rootCause.constraintViolations.map { violation ->
                        mapOf(
                            "property" to violation.property,
                            "value" to violation.value,
                            "message" to violation.constraint.name
                        )
                    }
                    call.respond(HttpStatusCode.BadRequest, mapOf("errors" to errors))
                }
                else -> {
                    // Handle other types of deserialization errors
                    call.respond(
                        HttpStatusCode.BadRequest,
                        mapOf("error" to (rootCause?.message ?: cause.message ?: "Bad Request"))
                    )
                }
            }
        }

        status(HttpStatusCode.NotFound) { call, status ->
            call.respond(
                status = status,
                message = mapOf("error" to "Resource not found")
            )
        }

        status(HttpStatusCode.Forbidden) { call, status ->
            call.respond(
                status = status,
                message = mapOf("error" to "Insufficient permissions")
            )
        }

        status(HttpStatusCode.Unauthorized) { call, status ->
            call.respond(
                status = status,
                message = mapOf("error" to "Authentication required")
            )
        }

        exception<Throwable> { call, cause ->
            call.respondText(text = "500: $cause" , status = HttpStatusCode.InternalServerError)
        }
    }
    install(Resources)

    registerUserRoutes()
    registerAuthRoutes()
}