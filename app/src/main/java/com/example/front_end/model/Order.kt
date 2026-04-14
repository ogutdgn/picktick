package com.example.front_end.model

import java.util.Date

enum class OrderStatus { PENDING, CONFIRMED, CANCELLED }

data class Order(
    val orderId: String,
    val buyerId: String,
    val sellerId: String,
    val ticketId: String,
    val totalPrice: Double,
    val status: OrderStatus = OrderStatus.PENDING,
    val createdAt: Date = Date()
)
