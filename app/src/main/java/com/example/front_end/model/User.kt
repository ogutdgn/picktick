package com.example.front_end.model

import java.util.Date

enum class UserRole { BUYER, SELLER, ADMIN }

data class User(
    val userId: String,
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: UserRole,
    val avatarUrl: String? = null,
    val isOnline: Boolean = false,
    val rating: Float = 0f,
    val reviewCount: Int = 0,
    val createdAt: Date = Date(),
    val isActive: Boolean = true
)
