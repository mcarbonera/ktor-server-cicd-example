package com.evennt.infrastructure.persistence.sql

import com.evennt.core.Settings
import com.evennt.infrastructure.persistence.sql.models.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseFactory {

    private val hikariDataSource: HikariDataSource by lazy {
        val config = HikariConfig().apply {
            jdbcUrl = Settings.JDBC_URL
            username = Settings.DB_USERNAME
            password = Settings.DB_PASSWORD
            driverClassName = Settings.DB_DRIVER
            maximumPoolSize = 10
            isAutoCommit = false
        }

        val datasource = HikariDataSource(config)
        Database.connect(datasource)
        transaction {
            SchemaUtils.create(UserTable)
        }

        datasource
    }

    fun init() {
        hikariDataSource
    }
}