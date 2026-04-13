package com.example.front_end.model

import java.util.Date

enum class ListingStatus { ACTIVE, SOLD }
enum class TicketType { DIGITAL }
enum class ProofType { CONFIRMATION_CODE }

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
    val ticketType: TicketType = TicketType.DIGITAL,
    val proofType: ProofType = ProofType.CONFIRMATION_CODE,
    val proofCode: String,
    val createdAt: Date = Date()
)
