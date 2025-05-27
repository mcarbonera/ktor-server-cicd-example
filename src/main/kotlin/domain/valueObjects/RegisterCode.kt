package com.evennt.domain.valueObjects

sealed class RegisterCode(val value: String) {
    abstract fun validate(): Boolean

    data class Individual(val individualCode: String) : RegisterCode(individualCode) {
        init {
            require(individualCode.matches(Regex("^[0-9]{11}\$"))) { "Invalid CPF format" }
        }

        override fun validate(): Boolean {
            return individualCode.isNotEmpty() && individualCode.length == 11
        }
    }

    data class Business(val companyCode: String) : RegisterCode(companyCode) {
        init {
            require(companyCode.matches(Regex("^[0-9]{14}\$"))) { "Invalid CNPJ format" }
        }

        override fun validate(): Boolean {
            return companyCode.isNotEmpty() && companyCode.length == 14
        }
    }
}