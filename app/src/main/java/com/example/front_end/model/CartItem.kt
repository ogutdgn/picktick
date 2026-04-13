package com.example.front_end.model

import java.util.Date

data class CartItem(
    val cartItemId: String,
    val userId: String,
    val ticketId: String,
    val addedAt: Date = Date()
)
