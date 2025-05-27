package com.evennt.core.types

sealed class Outcome<out R, out E> {
    data class Success<R>(val value: R): Outcome<R, Nothing>()
    data class Failure<E>(val reason: E): Outcome<Nothing, E>()
}