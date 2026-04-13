package com.example.front_end.service

import com.example.front_end.data.MockData
import com.example.front_end.model.ListingStatus
import com.example.front_end.model.ProofType
import com.example.front_end.model.TicketListing
import com.example.front_end.model.TicketType
import java.util.Date
import java.util.UUID

object TicketService {

    fun getAllActiveListings(): List<TicketListing> {
        return MockData.listings.filter { it.status == ListingStatus.ACTIVE }
    }

    fun getListingById(id: String): TicketListing? {
        return MockData.listings.find { it.id == id }
    }

    fun getListingsBySeller(sellerId: String): List<TicketListing> {
        return MockData.listings.filter { it.sellerId == sellerId }
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
        proofCode: String
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
            status = ListingStatus.ACTIVE,
            ticketType = TicketType.DIGITAL,
            proofType = ProofType.CONFIRMATION_CODE,
            proofCode = proofCode,
            createdAt = Date()
        )
        MockData.listings.add(newListing)
        return true
    }

    fun updateListing(updated: TicketListing): Boolean {
        val index = MockData.listings.indexOfFirst { it.id == updated.id }
        if (index == -1) return false
        MockData.listings[index] = updated
        return true
    }

    fun markAsSold(id: String): Boolean {
        val index = MockData.listings.indexOfFirst { it.id == id }
        if (index == -1) return false
        MockData.listings[index] = MockData.listings[index].copy(status = ListingStatus.SOLD)
        return true
    }

    fun deleteListing(id: String): Boolean {
        return MockData.listings.removeIf { it.id == id }
    }
}
