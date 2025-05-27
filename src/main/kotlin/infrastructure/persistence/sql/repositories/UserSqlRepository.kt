package com.evennt.infrastructure.persistence.sql.repositories

import com.evennt.domain.entities.User
import com.evennt.domain.repositories.UserRepository
import com.evennt.infrastructure.persistence.sql.dbQueryGlobal
import com.evennt.infrastructure.persistence.sql.models.UserModel
import com.evennt.infrastructure.persistence.sql.models.UserTable
import kotlinx.datetime.Clock
import java.util.UUID

class UserSqlRepository: UserRepository {
    override suspend fun createUser(user: User): User {
        val userModel = dbQueryGlobal { UserModel.new {
            auxiliarCode = user.auxiliarCode
            email = user.email.value
            password = user.password
            name = user.name
        } }
        return userModel.toDomain()
    }

    override suspend fun listUsers(): List<User> {
        return dbQueryGlobal { UserModel.all().map { it -> it.toDomain() } }
    }

    override suspend fun retrieveUser(id: String): User? {
        return dbQueryGlobal { UserModel.findById(UUID.fromString(id))?.toDomain() }
    }

    override suspend fun destroyUser(id: String): Boolean {
        return dbQueryGlobal {
            val user = UserModel.findById(UUID.fromString(id))
            return@dbQueryGlobal user?.let {
                it.deletedAt = Clock.System.now()
                true
            } ?: false
        }
    }

    override suspend fun updatedUser(id: String, user: Map<String, Any?>) {
        dbQueryGlobal {
            val existingUser = UserModel.findById(UUID.fromString(id))
            if (existingUser == null) {
                return@dbQueryGlobal
            }

            user.forEach { (key, value) ->
                when (key) {
                    "auxiliarCode" -> existingUser.auxiliarCode = value as String?
                    "email" -> existingUser.email = value as String
                    "password" -> existingUser.password = value as String
                    "name" -> existingUser.name = value as String
                }
            }
        }
    }

    override suspend fun retrieveUserByEmail(email: String): User? {
        return dbQueryGlobal { UserModel.find { UserTable.email eq email }.firstOrNull()?.toDomain() }
    }
}