package com.example.front_end.model

import java.util.Date

data class Review(
    val reviewId: String,
    val reviewerId: String,
    val targetId: String,
    val rating: Float,
    val createdAt: Date = Date()
)
