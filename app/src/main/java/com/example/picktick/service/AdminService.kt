package com.example.picktick.service

import com.example.picktick.data.DatabaseManager
import com.example.picktick.model.TicketListing
import com.example.picktick.model.User
import com.example.picktick.model.UserRole

object AdminService {

    fun getAllUsers(): List<User> {
        return DatabaseManager.users.getAll().filter { it.role != UserRole.ADMIN }
    }

    fun banUser(userId: String): Boolean {
        return DatabaseManager.users.setActive(userId, false)
    }

    fun unbanUser(userId: String): Boolean {
        return DatabaseManager.users.setActive(userId, true)
    }

    fun getAllListings(): List<TicketListing> {
        return DatabaseManager.listings.getAll()
    }

    fun deleteListing(listingId: String): Boolean {
        return TicketService.deleteListing(listingId)
    }
}
