package com.evennt.domain.services

interface HashingService {
    fun hash(value: String): String
    fun compare(value: String, hash: String): Boolean
}