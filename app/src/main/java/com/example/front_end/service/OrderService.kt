package com.example.front_end.service

import com.example.front_end.data.MockData
import com.example.front_end.model.Order
import com.example.front_end.model.OrderStatus
import com.example.front_end.model.TicketListing
import java.util.Date
import java.util.UUID

object OrderService {

    fun createOrders(buyerId: String, tickets: List<TicketListing>): List<Order> {
        val created = mutableListOf<Order>()
        tickets.forEach { ticket ->
            val order = Order(
                orderId = UUID.randomUUID().toString(),
                buyerId = buyerId,
                sellerId = ticket.sellerId,
                ticketId = ticket.id,
                totalPrice = ticket.price,
                status = OrderStatus.CONFIRMED,
                createdAt = Date()
            )
            MockData.orders.add(order)
            TicketService.markAsSold(ticket.id)
            created.add(order)
        }
        return created
    }

    fun getOrdersByBuyer(buyerId: String): List<Order> {
        return MockData.orders.filter { it.buyerId == buyerId }
    }

    fun getOrdersBySeller(sellerId: String): List<Order> {
        return MockData.orders.filter { it.sellerId == sellerId }
    }
}
