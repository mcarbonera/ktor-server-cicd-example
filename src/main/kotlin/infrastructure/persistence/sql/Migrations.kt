package com.evennt.infrastructure.persistence.sql

import com.evennt.core.Settings
import org.flywaydb.core.Flyway

fun migrateDatabase() {
    val flyway = Flyway
        .configure()
        .dataSource(
            Settings.JDBC_URL,
            Settings.DB_USERNAME,
            Settings.DB_PASSWORD
        )
        .locations("filesystem:src/main/resources/db/migration")
        .load()

    flyway.migrate()
}