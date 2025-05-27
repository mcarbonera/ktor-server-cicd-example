package com.evennt;

import com.evennt.infrastructure.persistence.sql.DatabaseFactory
import com.evennt.infrastructure.persistence.sql.migrateDatabase
import com.evennt.infrastructure.plugins.configureFrameworks
import com.evennt.infrastructure.plugins.configureSecurity
import com.evennt.presentation.plugins.configureHTTP
import com.evennt.presentation.plugins.configureRouting
import com.evennt.presentation.plugins.configureSerialization
import com.evennt.presentation.plugins.configureSockets
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    migrateDatabase()

    DatabaseFactory.init()

    configureSockets()
    configureFrameworks()
    configureSerialization()
    configureSecurity()
    configureHTTP()
    configureRouting()
}