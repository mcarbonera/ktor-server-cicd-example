package com.evennt.core

import io.github.cdimascio.dotenv.dotenv

object Settings {
    private val dotEnv = dotenv {
        ignoreIfMissing = true
    }

    val JWT_ACCESS_SECRET: String = dotEnv["JWT_ACCESS_SECRET"] ?: throw IllegalStateException("JWT_ACCESS_SECRET must be set in environment")
    val JWT_REFRESH_SECRET: String = dotEnv["JWT_REFRESH_SECRET"] ?: throw IllegalStateException("JWT_ACCESS_SECRET must be set in environment")
    val JWT_ISSUER: String = dotEnv["JWT_ISSUER"] ?: "http://localhost:8080"
    val JWT_AUDIENCE: String = dotEnv["JWT_AUDIENCE"] ?: "http://localhost:8080"
    val JWT_ACCESS_EXPIRATION: Long = dotEnv["JWT_ACCESS_EXPIRATION"]?.toLongOrNull() ?: (60L * 60 * 1000)
    val JWT_REFRESH_EXPIRATION: Long = dotEnv["JWT_REFRESH_EXPIRATION"]?.toLongOrNull() ?: (30L * 24 *60 * 60 * 1000)
    val JWT_REALM: String = dotEnv["JWT_REALM"] ?: "evennt"

    val JDBC_URL: String = dotEnv["DB_URL"]
    val DB_USERNAME: String = dotEnv["DB_USERNAME"]
    val DB_PASSWORD: String = dotEnv["DB_PASSWORD"]
    val DB_DRIVER: String = dotEnv["DB_DRIVER"]

}
