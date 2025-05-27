package com.evennt.presentation.validations

import org.valiktor.Constraint
import org.valiktor.Validator

class LowerCaseRequiredConstraint : Constraint {
    override val name: String = "LowerCaseRequired"
}

class UpperCaseRequiredConstraint : Constraint {
    override val name: String = "UpperCaseRequired"
}

class DigitRequiredConstraint : Constraint {
    override val name: String = "DigitRequired"
}

class SpecialCharRequiredConstraint : Constraint {
    override val name: String = "SpecialCharRequired"
}

fun <E> Validator<E>.Property<String?>.hasLowerCase(): Validator<E>.Property<String?> =
    this.validate(LowerCaseRequiredConstraint()) { it != null && it.any { c -> c.isLowerCase() } }

fun <E> Validator<E>.Property<String?>.hasUpperCase(): Validator<E>.Property<String?> =
    this.validate(UpperCaseRequiredConstraint()) { it != null && it.any { c -> c.isUpperCase() } }

fun <E> Validator<E>.Property<String?>.hasDigit(): Validator<E>.Property<String?> =
    this.validate(DigitRequiredConstraint()) { it != null && it.any { c -> c.isDigit() } }

fun <E> Validator<E>.Property<String?>.hasSpecialChar(): Validator<E>.Property<String?> =
    this.validate(SpecialCharRequiredConstraint()) { it != null && it.any { c -> !c.isLetterOrDigit() } }