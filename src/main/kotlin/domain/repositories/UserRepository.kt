package com.evennt.domain.repositories

import com.evennt.domain.entities.User

interface UserRepository {
    suspend fun createUser(user: User): User
    suspend fun listUsers(): List<User>
    suspend fun retrieveUser(id: String): User?
    suspend fun destroyUser(id: String): Boolean
    suspend fun updatedUser(id: String, user: Map<String, Any?>)
    suspend fun retrieveUserByEmail(email: String): User?
}