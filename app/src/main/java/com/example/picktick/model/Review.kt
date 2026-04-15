package com.example.picktick.model

import java.util.Date

data class Review(
    val reviewId: String,
    val reviewerId: String,
    val targetId: String,
    val rating: Float,
    val createdAt: Date = Date()
)
