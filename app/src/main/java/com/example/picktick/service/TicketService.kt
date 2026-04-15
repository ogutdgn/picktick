package com.example.picktick.service

import com.example.picktick.data.DatabaseManager
import com.example.picktick.model.ListingStatus
import com.example.picktick.model.TicketListing
import java.util.Date
import java.util.UUID

object TicketService {

    fun getAllActiveListings(): List<TicketListing> {
        return DatabaseManager.listings.getAllActive()
    }

    fun getListingById(id: String): TicketListing? {
        return DatabaseManager.listings.findById(id)
    }

    fun getListingsBySeller(sellerId: String): List<TicketListing> {
        return DatabaseManager.listings.findBySeller(sellerId)
    }

    fun createListing(
        title: String,
        eventName: String,
        date: String,
        time: String,
        location: String,
        price: Double,
        category: String,
        seat: String,
        description: String,
        sellerId: String,
        proofCode: String,
        published: Boolean = true
    ): Boolean {
        val newListing = TicketListing(
            id = UUID.randomUUID().toString(),
            title = title,
            eventName = eventName,
            date = date,
            time = time,
            location = location,
            price = price,
            category = category,
            seat = seat,
            description = description,
            sellerId = sellerId,
            status = if (published) ListingStatus.ACTIVE else ListingStatus.UNPUBLISHED,
            proofCode = proofCode,
            createdAt = Date()
        )
        return DatabaseManager.listings.insert(newListing)
    }

    fun updateListing(updated: TicketListing): Boolean {
        return DatabaseManager.listings.update(updated)
    }

    fun togglePublish(id: String): Boolean {
        val listing = DatabaseManager.listings.findById(id) ?: return false
        val newStatus = if (listing.status == ListingStatus.ACTIVE) ListingStatus.UNPUBLISHED else ListingStatus.ACTIVE
        return DatabaseManager.listings.updateStatus(id, newStatus)
    }

    fun markAsSold(id: String): Boolean {
        return DatabaseManager.listings.updateStatus(id, ListingStatus.SOLD)
    }

    fun deleteListing(id: String): Boolean {
        return DatabaseManager.listings.delete(id)
    }
}
