package com.evennt.domain.entities

import com.evennt.core.helpers.getLocalizedDateTime
import com.evennt.domain.valueObjects.AuthTokenPayload
import com.evennt.domain.valueObjects.Email
import kotlinx.datetime.LocalDateTime
import java.util.*
import kotlin.uuid.ExperimentalUuidApi

/**
 * Represents a user in the system.
 *
 * @property id Unique identifier of the user.
 * @property email Email address (encapsulated in a value object).
 * @property password Hashed password of the user.
 * @property name Full name of the user.
 * @property active Whether the account is active.
 * @property emailConfirmed Whether the email address was confirmed.
 * @property lastCredentialInvalidation Timestamp of the last time credentials were invalidated.
 * @property auxiliarCode Optional auxiliary code.
 * @property contactId Foreign key to the contact entity.
 * @property companyId Foreign key to the company entity.
 * @property contact Optional nested contact object.
 * @property company Optional nested company object.
 * @property createdAt Timestamp of when the user was created.
 * @property updatedAt Timestamp of when the user was last updated.
 */
@OptIn(ExperimentalUuidApi::class)
data class User(
    val id: String? = null,
    val email: Email,
    val password: String,
    val name: String,
    val active: Boolean = true,
    val emailConfirmed: Boolean = true,
    val lastCredentialInvalidation: Date? = null,
    // optionals
    val auxiliarCode: String? = null,
    // fks
    val contactId: String? = null,
    val companyId: String? = null,
    // nested objects
    //val contact: Contact? = null,
    //val company: Company? = null,
    // timestamps
    val createdAt: LocalDateTime = getLocalizedDateTime(),
    val updatedAt: LocalDateTime = getLocalizedDateTime(),
) {
    /**
     * Checks whether the user is allowed to log in.
     *
     * To log in, the user must:
     * - Have an active account.
     * - Have a confirmed email address.
     *
     * @return true if the user meets the login conditions.
     */
    fun canLogin(): Boolean = active && emailConfirmed

    /**
     * Checks whether a token is valid for this user.
     *
     * If the user has had credentials invalidated after the token was issued, the token is considered invalid.
     *
     * @param token The token payload to validate.
     * @return true if the token is valid, false otherwise.
     */
    fun canUseToken(token: AuthTokenPayload): Boolean = lastCredentialInvalidation?.time?.let { token.iat > it } ?: true
}
