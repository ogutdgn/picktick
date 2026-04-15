package com.example.picktick.model

import java.util.Date

enum class ListingStatus { ACTIVE, SOLD, UNPUBLISHED }

data class TicketListing(
    val id: String,
    val title: String,
    val eventName: String,
    val date: String,
    val time: String,
    val location: String,
    val price: Double,
    val category: String,
    val seat: String,
    val description: String,
    val sellerId: String,
    val status: ListingStatus = ListingStatus.ACTIVE,
    val proofCode: String,
    val createdAt: Date = Date()
)
