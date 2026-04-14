package com.example.front_end.data

import com.example.front_end.model.*
import java.util.Date

object MockData {

    val users = mutableListOf(
        User(
            userId = "U001",
            name = "Alex Johnson",
            email = "user@picktick.com",
            passwordHash = "user-123",
            role = UserRole.USER,
            isOnline = true,
            rating = 4.5f,
            reviewCount = 8
        ),
        User(
            userId = "U002",
            name = "Sarah Miller",
            email = "user2@picktick.com",
            passwordHash = "user2-123",
            role = UserRole.USER,
            isOnline = false,
            rating = 4.8f,
            reviewCount = 23
        ),
        User(
            userId = "U003",
            name = "Admin User",
            email = "admin@picktick.com",
            passwordHash = "admin123",
            role = UserRole.ADMIN,
            isOnline = true,
            rating = 0f,
            reviewCount = 0
        )
    )

    val listings = mutableListOf(
        TicketListing(
            id = "T001",
            title = "Music Festival 2026",
            eventName = "Music Festival of New York 2026",
            date = "Oct 12, 2026",
            time = "7:00 PM",
            location = "New York, NY",
            price = 120.0,
            category = "Music",
            seat = "General Admission",
            description = "General admission to the biggest music festival of the year.",
            sellerId = "U002",
            status = ListingStatus.ACTIVE,
            proofCode = "MFY-2026-XK9P"
        ),
        TicketListing(
            id = "T002",
            title = "Tech Expo of Texas",
            eventName = "Tech Expo of Texas 2026",
            date = "Nov 05, 2026",
            time = "9:00 AM",
            location = "Arlington, TX",
            price = 50.0,
            category = "Expo",
            seat = "N/A",
            description = "Full access to all booths and keynote sessions.",
            sellerId = "U002",
            status = ListingStatus.ACTIVE,
            proofCode = "TET-2026-AB3R"
        ),
        TicketListing(
            id = "T003",
            title = "NBA Finals Game 3",
            eventName = "NBA Finals 2026",
            date = "Jun 10, 2026",
            time = "8:00 PM",
            location = "Dallas, TX",
            price = 350.0,
            category = "Sports",
            seat = "Section B, Row 12",
            description = "Lower bowl seats with an excellent view of the court.",
            sellerId = "U002",
            status = ListingStatus.ACTIVE,
            proofCode = "NBA-FIN-2026-C7X"
        ),
        TicketListing(
            id = "T004",
            title = "Hamilton Broadway",
            eventName = "Hamilton - The Musical",
            date = "Sep 20, 2026",
            time = "7:30 PM",
            location = "Chicago, IL",
            price = 200.0,
            category = "Theater",
            seat = "Row F, Seat 14",
            description = "Orchestra section seats for the award-winning musical.",
            sellerId = "U002",
            status = ListingStatus.ACTIVE,
            proofCode = "HAM-2026-QW7T"
        ),
        TicketListing(
            id = "T005",
            title = "Train to Chicago",
            eventName = "Amtrak Texas Eagle",
            date = "Dec 20, 2025",
            time = "6:30 PM",
            location = "Fort Worth, TX",
            price = 85.0,
            category = "Travel",
            seat = "Car 4, Seat 22A",
            description = "Window seat on the Texas Eagle route from Fort Worth to Chicago.",
            sellerId = "U002",
            status = ListingStatus.SOLD,
            proofCode = "AMT-2026-ZP2M"
        ),
        TicketListing(
            id = "T006",
            title = "Jazz Night Live",
            eventName = "Dallas Jazz Festival 2026",
            date = "Aug 03, 2026",
            time = "8:00 PM",
            location = "Dallas, TX",
            price = 75.0,
            category = "Music",
            seat = "Table 12",
            description = "Seated table experience at the annual Dallas Jazz Festival.",
            sellerId = "U002",
            status = ListingStatus.ACTIVE,
            proofCode = "DJF-2026-LM4Q"
        )
    )

    val threads = mutableListOf(
        ChatThread(
            threadId = "TH001",
            participantIds = listOf("U001", "U002"),
            lastMessage = "Is the music festival ticket still available?",
            lastMessageTime = Date()
        )
    )

    val messages = mutableListOf(
        Message(
            messageId = "M001",
            threadId = "TH001",
            senderId = "U001",
            content = "Is the music festival ticket still available?",
            status = MessageStatus.DELIVERED
        ),
        Message(
            messageId = "M002",
            threadId = "TH001",
            senderId = "U002",
            content = "Yes it is! Would you like to negotiate the price?",
            status = MessageStatus.DELIVERED
        ),
        Message(
            messageId = "M003",
            threadId = "TH001",
            senderId = "U001",
            content = "Could you go down to $100?",
            status = MessageStatus.DELIVERED
        )
    )

    val orders = mutableListOf(
        Order(
            orderId = "O001",
            buyerId = "U001",
            sellerId = "U002",
            ticketId = "T005",
            totalPrice = 85.0,
            status = OrderStatus.CONFIRMED
        )
    )

    val reviews = mutableListOf(
        Review(
            reviewId = "R001",
            reviewerId = "U001",
            targetId = "U002",
            rating = 5.0f
        )
    )

    val cartItems = mutableListOf<CartItem>()
}
