package com.evennt.infrastructure.persistence.sql

import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


/**
 * Helper function to execute a database query on the global (admin) database.
 * This function is used to interact with tables or resources that are not tenant-specific.
 *
 * @param T The type of the result returned by the database query. Typically, this could be
 *          a domain model or DTO (Data Transfer Object).
 * @param block A suspending lambda that contains the database query logic. This block will
 *              be executed within a suspended transaction on the global database.
 *
 * @return The result of the database query. The type will be inferred from the block parameter.
 *
 * Example usage:
 * ```
 * val user = dbQueryGlobal { Users.select { Users.id eq 1 }.firstOrNull()?.toDomain() }
 * ```
 */
suspend fun <T> dbQueryGlobal(block: suspend () -> T): T {
    return newSuspendedTransaction(Dispatchers.IO) {
        block()
    }
}

/**
 * Helper function to execute a database query on a tenant-specific schema.
 * This function connects to a schema specific to the provided tenant name.
 * Tenant-specific queries should be run using this function, ensuring that each tenant's data is isolated.
 *
 * @param T The type of the result returned by the database query. Typically, this could be
 *          a domain model or DTO (Data Transfer Object) related to a specific tenant.
 * @param tenant The unique identifier (name) of the tenant whose schema the query should be executed on.
 * @param block A suspending lambda that contains the database query logic. This block will
 *              be executed within a suspended transaction on the tenant's schema.
 *
 * @return The result of the database query. The type will be inferred from the block parameter.
 *
 * Example usage:
 * ```
 * val clients = dbQueryTenant("tenant_123") { Clients.selectAll().map { it.toDomain() } }
 * ```
 */
suspend fun <T> dbQueryTenant(tenant: String, block: suspend () -> T): T {
    return newSuspendedTransaction {
        exec("SET search_path TO \"$tenant\"")
        block()
    }
}