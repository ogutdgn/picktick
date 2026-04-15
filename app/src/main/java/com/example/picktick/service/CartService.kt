package com.example.picktick.service

import com.example.picktick.data.DatabaseManager
import com.example.picktick.model.CartItem
import com.example.picktick.model.TicketListing
import java.util.UUID

object CartService {

    fun getCart(userId: String): List<CartItem> {
        return DatabaseManager.carts.getByUser(userId)
    }

    fun getCartListings(userId: String): List<TicketListing> {
        val cartTicketIds = getCart(userId).map { it.ticketId }
        return cartTicketIds.mapNotNull { TicketService.getListingById(it) }
    }

    fun addItem(ticketId: String, userId: String): Boolean {
        if (DatabaseManager.carts.exists(ticketId, userId)) return false
        if (DatabaseManager.orders.existsConfirmed(ticketId, userId)) return false
        val cartItem = CartItem(
            cartItemId = UUID.randomUUID().toString(),
            userId = userId,
            ticketId = ticketId
        )
        return DatabaseManager.carts.insert(cartItem)
    }

    fun removeItem(ticketId: String, userId: String) {
        DatabaseManager.carts.delete(ticketId, userId)
    }

    fun clearCart(userId: String) {
        DatabaseManager.carts.deleteByUser(userId)
    }

    fun clearCart() {
        DatabaseManager.carts.deleteAll()
    }

    fun isInCart(ticketId: String, userId: String): Boolean {
        return DatabaseManager.carts.exists(ticketId, userId)
    }

    fun isPurchased(ticketId: String, userId: String): Boolean {
        return DatabaseManager.orders.existsConfirmed(ticketId, userId)
    }

    fun getTotal(userId: String): Double {
        return getCartListings(userId).sumOf { it.price }
    }
}
