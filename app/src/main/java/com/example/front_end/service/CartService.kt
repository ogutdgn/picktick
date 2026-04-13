package com.example.front_end.service

import com.example.front_end.data.MockData
import com.example.front_end.model.CartItem
import com.example.front_end.model.OrderStatus
import com.example.front_end.model.TicketListing
import java.util.UUID

object CartService {

    fun getCart(userId: String): List<CartItem> {
        return MockData.cartItems.filter { it.userId == userId }
    }

    fun getCartListings(userId: String): List<TicketListing> {
        val cartTicketIds = getCart(userId).map { it.ticketId }
        return MockData.listings.filter { it.id in cartTicketIds }
    }

    fun addItem(ticketId: String, userId: String): Boolean {
        val alreadyInCart = MockData.cartItems.any { it.ticketId == ticketId && it.userId == userId }
        val alreadyPurchased = MockData.orders.any {
            it.ticketId == ticketId && it.buyerId == userId && it.status == OrderStatus.CONFIRMED
        }
        if (alreadyInCart || alreadyPurchased) return false

        val cartItem = CartItem(
            cartItemId = UUID.randomUUID().toString(),
            userId = userId,
            ticketId = ticketId
        )
        MockData.cartItems.add(cartItem)
        return true
    }

    fun removeItem(ticketId: String, userId: String) {
        MockData.cartItems.removeIf { it.ticketId == ticketId && it.userId == userId }
    }

    fun clearCart(userId: String) {
        MockData.cartItems.removeIf { it.userId == userId }
    }

    fun clearCart() {
        MockData.cartItems.clear()
    }

    fun isInCart(ticketId: String, userId: String): Boolean {
        return MockData.cartItems.any { it.ticketId == ticketId && it.userId == userId }
    }

    fun isPurchased(ticketId: String, userId: String): Boolean {
        return MockData.orders.any {
            it.ticketId == ticketId && it.buyerId == userId && it.status == OrderStatus.CONFIRMED
        }
    }

    fun getTotal(userId: String): Double {
        return getCartListings(userId).sumOf { it.price }
    }
}
