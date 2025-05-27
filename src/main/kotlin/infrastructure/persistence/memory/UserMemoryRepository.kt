package com.evennt.infrastructure.persistence.memory

import com.evennt.domain.entities.User
import com.evennt.domain.repositories.UserRepository
import java.util.UUID

class UserMemoryRepository: UserRepository {
    private val users = mutableListOf<User>()

    override suspend  fun createUser(user: User): User {
        val userWithId = user.copy(id = UUID.randomUUID().toString())
        users.add(userWithId)
        return  userWithId
    }

    override suspend fun listUsers(): List<User> {
        return users
    }

    override suspend fun retrieveUser(id: String): User? {
        val user = users.find { it.id == id }
        return user
    }

    override suspend fun destroyUser(id: String): Boolean {
        val user = retrieveUser(id)
        if (user != null) {
            users.remove(user)
            return true
        }
        return false
    }

    override suspend fun updatedUser(id: String, user: Map<String, Any?>) {
        TODO("Not yet implemented")
    }

    override suspend fun retrieveUserByEmail(email: String): User? {
        val user = users.find { it.email.value == email }
        return user
    }
}