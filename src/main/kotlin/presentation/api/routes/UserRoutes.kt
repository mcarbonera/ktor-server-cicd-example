package com.evennt.presentation.api.routes

import com.evennt.application.useCases.ListUsersUseCase
import com.evennt.infrastructure.persistence.memory.UserMemoryRepository
import com.evennt.presentation.api.schemas.ListUsersResponseSchema
import io.ktor.http.HttpStatusCode
import io.ktor.resources.Resource
import io.ktor.server.application.Application
import io.ktor.server.resources.get
import io.ktor.server.response.respond
import io.ktor.server.routing.routing
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory

@Serializable
@Resource("/users")
class UserResource

fun Application.registerUserRoutes() {

    val userRepository = UserMemoryRepository()
    val listUsersUseCase = ListUsersUseCase(userRepository)

    routing {
        get<UserResource> {
            //val logger = LoggerFactory.getLogger(Application::class.java)
            //logger.error("aquiiiiiiiiiiii")
            val users = listUsersUseCase.execute()
            val response: List<ListUsersResponseSchema> = users.map { ListUsersResponseSchema(
                id=it.id,
                email = it.email,
                createdAt = it.createdAt.toString(),
                updatedAt = it.updatedAt.toString()
            ) }
            call.respond(HttpStatusCode.OK, response)
        }
    }
}