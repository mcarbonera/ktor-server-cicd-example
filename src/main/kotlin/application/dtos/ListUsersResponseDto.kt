package com.evennt.application.dtos

import kotlinx.datetime.LocalDateTime

data class ListUsersResponseDto(
    val id: String,
    val email: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
)
