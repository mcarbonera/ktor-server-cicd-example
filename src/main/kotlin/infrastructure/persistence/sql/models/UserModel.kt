package com.evennt.infrastructure.persistence.sql.models

import com.evennt.domain.entities.User
import com.evennt.domain.valueObjects.Email
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.kotlin.datetime.*
import java.util.*

object UserTable: UUIDTable() {

    var auxiliarCode = varchar("auxiliar_code", 255).uniqueIndex().nullable()
    var email = varchar("email", 255).uniqueIndex()
    var password = varchar("password", 255)
    var name = varchar("name", 255)
    var createdAt = timestamp("created_at").defaultExpression(CurrentTimestamp)
    var updatedAt = timestamp("updated_at").defaultExpression(CurrentTimestamp)
    var deletedAt = timestamp("deleted_at").nullable()
}

class UserModel(id: EntityID<UUID>): UUIDEntity(id) {

    companion object: UUIDEntityClass<UserModel>(UserTable)

    var auxiliarCode by UserTable.auxiliarCode
    var email by UserTable.email
    var password by UserTable.password
    var name by UserTable.name
    var createdAt by UserTable.createdAt
    var updatedAt by UserTable.updatedAt
    var deletedAt by UserTable.deletedAt

    fun toDomain(): User = User(
        id = this.id.value.toString(),
        auxiliarCode = this.auxiliarCode,
        email = Email(this.email),
        password = this.password,
        name = this.name,
        createdAt = this.createdAt.toLocalDateTime(TimeZone.currentSystemDefault()),
        updatedAt = this.updatedAt.toLocalDateTime(TimeZone.currentSystemDefault())
    )
}