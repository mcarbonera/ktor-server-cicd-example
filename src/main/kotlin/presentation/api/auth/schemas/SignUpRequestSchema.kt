package com.evennt.presentation.api.auth.schemas

import com.evennt.presentation.validations.hasDigit
import com.evennt.presentation.validations.hasLowerCase
import com.evennt.presentation.validations.hasSpecialChar
import com.evennt.presentation.validations.hasUpperCase
import kotlinx.serialization.Serializable
import org.valiktor.functions.hasSize
import org.valiktor.functions.isEmail
import org.valiktor.functions.isNotNull
import org.valiktor.validate

@Serializable
data class SignUpRequestSchema(val email: String, val password: String, val name: String) {
    init {
        validate(this) {
            validate(SignUpRequestSchema::email).isNotNull().isEmail()
            validate(SignUpRequestSchema::password).isNotNull().hasSize(min = 8).hasLowerCase().hasUpperCase()
                .hasDigit().hasSpecialChar()
            validate(SignUpRequestSchema::name).isNotNull().hasSize(min=4, max=100)
        }
    }
}