package com.evennt.application.services

interface AuthGenerationService {

    fun createAccessToken(sub: String): String
    fun createRefreshToken(sub: String): String
}