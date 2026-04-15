package com.example.picktick.service

import com.example.picktick.data.DatabaseManager
import com.example.picktick.model.Order
import com.example.picktick.model.OrderStatus
import com.example.picktick.model.TicketListing
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
            DatabaseManager.orders.insert(order)
            TicketService.markAsSold(ticket.id)
            created.add(order)
        }
        return created
    }

    fun getOrdersByBuyer(buyerId: String): List<Order> {
        return DatabaseManager.orders.getByBuyer(buyerId)
    }

    fun getOrdersBySeller(sellerId: String): List<Order> {
        return DatabaseManager.orders.getBySeller(sellerId)
    }
}
