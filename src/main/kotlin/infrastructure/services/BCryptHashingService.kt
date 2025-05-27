package com.evennt.infrastructure.services

import com.evennt.domain.services.HashingService
import org.mindrot.jbcrypt.BCrypt

class BCryptHashingService: HashingService {
    override fun hash(value: String): String {
        val salt = BCrypt.gensalt()
        return BCrypt.hashpw(value, salt)
    }

    override fun compare(value: String, hash: String): Boolean {
        return BCrypt.checkpw(value, hash)
    }
}