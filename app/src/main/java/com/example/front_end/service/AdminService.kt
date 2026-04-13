package com.example.front_end.service

import com.example.front_end.data.MockData
import com.example.front_end.model.TicketListing
import com.example.front_end.model.User

object AdminService {

    fun getAllUsers(): List<User> {
        return MockData.users.filter { it.role != com.example.front_end.model.UserRole.ADMIN }
    }

    fun banUser(userId: String): Boolean {
        val index = MockData.users.indexOfFirst { it.userId == userId }
        if (index == -1) return false
        MockData.users[index] = MockData.users[index].copy(isActive = false)
        return true
    }

    fun unbanUser(userId: String): Boolean {
        val index = MockData.users.indexOfFirst { it.userId == userId }
        if (index == -1) return false
        MockData.users[index] = MockData.users[index].copy(isActive = true)
        return true
    }

    fun getAllListings(): List<TicketListing> {
        return MockData.listings.toList()
    }

    fun deleteListing(listingId: String): Boolean {
        return TicketService.deleteListing(listingId)
    }
}
