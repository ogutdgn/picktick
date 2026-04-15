package com.example.picktick.service

import com.example.picktick.data.DatabaseManager
import com.example.picktick.model.*
import java.util.Date

object SeedData {

    fun populate() {
        if (DatabaseManager.users.findByEmail("admin@picktick.com") != null) return

        val now = Date()
        val day = 86_400_000L

        val admin = User(userId = "u-admin", name = "Admin", email = "admin@picktick.com", passwordHash = "admin123", role = UserRole.ADMIN)
        val u1 = User(userId = "u-001", name = "Dogan Ogut", email = "user1@picktick.com", passwordHash = "user123", role = UserRole.USER)
        val u2 = User(userId = "u-002", name = "Keegan Boutwell", email = "user2@picktick.com", passwordHash = "user123", role = UserRole.USER)
        val u3 = User(userId = "u-003", name = "Phuc Nguyen", email = "user3@picktick.com", passwordHash = "user123", role = UserRole.USER)
        val u4 = User(userId = "u-004", name = "Jake Milligan", email = "user4@picktick.com", passwordHash = "user123", role = UserRole.USER)

        listOf(admin, u1, u2, u3, u4).forEach { DatabaseManager.users.insert(it) }

        val listings = listOf(
            TicketListing(
                id = "l-001", title = "Coldplay - Music of the Spheres Tour",
                eventName = "Music of the Spheres World Tour", date = "Jul 15, 2026", time = "20:00",
                location = "Allegiant Stadium, Las Vegas", price = 250.0, category = "Music",
                seat = "Block A, Row 5, Seat 12", description = "Front area ticket with incredible stage view. Floor standing zone.",
                sellerId = u1.userId, proofCode = "CW-2026-A5-12", status = ListingStatus.ACTIVE
            ),
            TicketListing(
                id = "l-002", title = "Premier League: Man City vs Arsenal",
                eventName = "Premier League Matchday 32", date = "Apr 26, 2026", time = "17:30",
                location = "Etihad Stadium, Manchester", price = 180.0, category = "Sports",
                seat = "North Stand, Row 8, Seat 44", description = "Top of the table clash. Incredible atmosphere guaranteed.",
                sellerId = u1.userId, proofCode = "MCFC-ARS-MD32-44", status = ListingStatus.ACTIVE
            ),
            TicketListing(
                id = "l-003", title = "Hamilton - The Musical",
                eventName = "Hamilton Broadway Revival", date = "Jun 22, 2026", time = "18:30",
                location = "Richard Rodgers Theatre, New York", price = 320.0, category = "Theater",
                seat = "Balcony Row 2, Seat 7", description = "Premium balcony seat for the iconic Hamilton musical. Unobstructed view.",
                sellerId = u2.userId, proofCode = "HAM-NY-B2S7", status = ListingStatus.ACTIVE
            ),
            TicketListing(
                id = "l-004", title = "Imagine Dragons - Loom World Tour",
                eventName = "Loom World Tour 2026", date = "Sep 12, 2026", time = "21:00",
                location = "Madison Square Garden, New York", price = 210.0, category = "Music",
                seat = "Pit Area - General Admission", description = "Standing pit ticket. Up close with the band.",
                sellerId = u2.userId, proofCode = "ID-LOOM-PIT-01", status = ListingStatus.ACTIVE
            ),
            TicketListing(
                id = "l-005", title = "AWS re:Invent 2026",
                eventName = "Amazon Web Services Annual Conference", date = "Dec 01, 2026", time = "09:00",
                location = "The Venetian, Las Vegas", price = 1800.0, category = "Expo",
                seat = "Full Conference Pass", description = "Full access pass including keynotes, workshops, and expo hall.",
                sellerId = u3.userId, proofCode = "AWS-REINVENT-26-FC", status = ListingStatus.ACTIVE
            ),
            TicketListing(
                id = "l-006", title = "Roland Garros Finals 2026",
                eventName = "French Open Men's Final", date = "Jun 08, 2026", time = "15:00",
                location = "Court Philippe-Chatrier, Paris", price = 450.0, category = "Sports",
                seat = "Court Side, Section J, Seat 12", description = "Men's final ticket on the show court. Once in a lifetime experience.",
                sellerId = u3.userId, proofCode = "RG-2026-FIN-J12", status = ListingStatus.ACTIVE
            ),
            TicketListing(
                id = "l-007", title = "NBA Finals 2026 - Game 7",
                eventName = "NBA Finals 2026", date = "Jun 21, 2026", time = "20:00",
                location = "Chase Center, San Francisco", price = 650.0, category = "Sports",
                seat = "Lower Bowl, Section 110, Row 15", description = "Deciding Game 7 of the NBA Finals. Best seats in the lower bowl.",
                sellerId = u4.userId, proofCode = "NBA-FIN-G7-S110R15", status = ListingStatus.ACTIVE
            ),
            TicketListing(
                id = "l-008", title = "Coachella Valley Music Festival",
                eventName = "Coachella 2026 - Weekend 1", date = "Apr 11, 2026", time = "12:00",
                location = "Empire Polo Club, Indio CA", price = 380.0, category = "Music",
                seat = "General Admission - Weekend 1", description = "Full weekend GA wristband. All stages, camping included.",
                sellerId = u4.userId, proofCode = "COACHELLA-W1-GA-26", status = ListingStatus.ACTIVE
            ),
            TicketListing(
                id = "l-009", title = "San Diego Comic-Con 2026",
                eventName = "Comic-Con International 2026", date = "Jul 24, 2026", time = "10:00",
                location = "San Diego Convention Center", price = 85.0, category = "Expo",
                seat = "4-Day Badge - General Admission", description = "4-day badge for all exhibit floors and panels.",
                sellerId = u1.userId, proofCode = "SDCC-2026-4DAY-GA", status = ListingStatus.UNPUBLISHED
            ),
            TicketListing(
                id = "l-010", title = "Wimbledon Finals 2026",
                eventName = "The Championships Wimbledon - Men's Final", date = "Jul 14, 2026", time = "14:00",
                location = "All England Club, London", price = 520.0, category = "Sports",
                seat = "Centre Court, Row H, Seat 22", description = "Centre Court debenture seat for the Men's Final.",
                sellerId = u2.userId, proofCode = "WIMB-2026-CC-H22", status = ListingStatus.UNPUBLISHED
            ),
            TicketListing(
                id = "l-011", title = "Google I/O 2026 Developer Conference",
                eventName = "Google I/O 2026", date = "May 20, 2026", time = "09:00",
                location = "Shoreline Amphitheatre, Mountain View", price = 299.0, category = "Expo",
                seat = "General Admission - All Sessions", description = "Full access to all keynotes, sessions, and codelabs.",
                sellerId = u3.userId, proofCode = "GIO-2026-GA-SESS", status = ListingStatus.UNPUBLISHED
            ),
            TicketListing(
                id = "l-012", title = "Super Bowl LX",
                eventName = "Super Bowl LX - 2026", date = "Feb 08, 2026", time = "18:30",
                location = "Levi's Stadium, Santa Clara", price = 800.0, category = "Sports",
                seat = "Section 201, Row 20, Seat 5", description = "Upper deck end zone ticket. Great view of both end zones.",
                sellerId = u4.userId, proofCode = "SB60-S201-R20-S5", status = ListingStatus.UNPUBLISHED
            ),
            TicketListing(
                id = "l-013", title = "Arctic Monkeys - The Car World Tour",
                eventName = "The Car World Tour", date = "Nov 14, 2025", time = "20:30",
                location = "Kia Forum, Los Angeles", price = 190.0, category = "Music",
                seat = "GA Standing Floor", description = "General admission standing ticket. Incredible show.",
                sellerId = u1.userId, proofCode = "AM-CAR-LA-GA", status = ListingStatus.SOLD,
                createdAt = Date(now.time - 90 * day)
            ),
            TicketListing(
                id = "l-014", title = "Formula 1 Miami Grand Prix",
                eventName = "F1 Miami Grand Prix 2025", date = "May 04, 2025", time = "15:00",
                location = "Miami International Autodrome", price = 380.0, category = "Sports",
                seat = "Grandstand 7, Row C, Seat 18", description = "Full race weekend grandstand ticket. Amazing track view.",
                sellerId = u2.userId, proofCode = "F1-MIA-GS7-C18", status = ListingStatus.SOLD,
                createdAt = Date(now.time - 60 * day)
            ),
            TicketListing(
                id = "l-015", title = "Beyonce Renaissance World Tour",
                eventName = "Renaissance World Tour 2025", date = "Aug 22, 2025", time = "19:00",
                location = "SoFi Stadium, Los Angeles", price = 275.0, category = "Music",
                seat = "Floor - Section B, Row 12", description = "Floor ticket with great view of the main and B-stage.",
                sellerId = u3.userId, proofCode = "BEY-REN-FLR-B12", status = ListingStatus.SOLD,
                createdAt = Date(now.time - 55 * day)
            ),
            TicketListing(
                id = "l-016", title = "UEFA Champions League Final",
                eventName = "UEFA Champions League Final 2025", date = "May 31, 2025", time = "21:00",
                location = "Allianz Arena, Munich", price = 420.0, category = "Sports",
                seat = "Category 2, Block 218, Row 10", description = "Category 2 ticket behind the goal. Electric atmosphere.",
                sellerId = u4.userId, proofCode = "UCL-FIN-218-R10", status = ListingStatus.SOLD,
                createdAt = Date(now.time - 45 * day)
            )
        )
        listings.forEach { DatabaseManager.listings.insert(it) }

        val orders = listOf(
            Order(
                orderId = "o-001", ticketId = "l-013",
                buyerId = u2.userId, sellerId = u1.userId,
                totalPrice = 190.0, status = OrderStatus.CONFIRMED,
                createdAt = Date(now.time - 85 * day)
            ),
            Order(
                orderId = "o-002", ticketId = "l-014",
                buyerId = u3.userId, sellerId = u2.userId,
                totalPrice = 380.0, status = OrderStatus.CONFIRMED,
                createdAt = Date(now.time - 55 * day)
            ),
            Order(
                orderId = "o-003", ticketId = "l-015",
                buyerId = u4.userId, sellerId = u3.userId,
                totalPrice = 275.0, status = OrderStatus.CONFIRMED,
                createdAt = Date(now.time - 50 * day)
            ),
            Order(
                orderId = "o-004", ticketId = "l-016",
                buyerId = u1.userId, sellerId = u4.userId,
                totalPrice = 420.0, status = OrderStatus.CONFIRMED,
                createdAt = Date(now.time - 40 * day)
            )
        )
        orders.forEach { DatabaseManager.orders.insert(it) }

        val reviews = listOf(
            Review(reviewId = "r-001", reviewerId = u2.userId, targetId = u1.userId, rating = 4.5f, createdAt = Date(now.time - 80 * day)),
            Review(reviewId = "r-002", reviewerId = u3.userId, targetId = u2.userId, rating = 5.0f, createdAt = Date(now.time - 48 * day)),
            Review(reviewId = "r-003", reviewerId = u4.userId, targetId = u3.userId, rating = 4.0f, createdAt = Date(now.time - 44 * day)),
            Review(reviewId = "r-004", reviewerId = u1.userId, targetId = u4.userId, rating = 4.5f, createdAt = Date(now.time - 35 * day))
        )
        reviews.forEach { DatabaseManager.reviews.insert(it) }

        DatabaseManager.users.updateRating(u1.userId, 4.5f, 1)
        DatabaseManager.users.updateRating(u2.userId, 5.0f, 1)
        DatabaseManager.users.updateRating(u3.userId, 4.0f, 1)
        DatabaseManager.users.updateRating(u4.userId, 4.5f, 1)

        val t1 = ChatThread(threadId = "t-001", participantIds = listOf(u1.userId, u2.userId), lastMessage = "Thanks! Enjoy the show!", lastMessageTime = Date(now.time - 84 * day))
        val t2 = ChatThread(threadId = "t-002", participantIds = listOf(u1.userId, u3.userId), lastMessage = "Yes, the seat is still available!", lastMessageTime = Date(now.time - 2 * day))
        val t3 = ChatThread(threadId = "t-003", participantIds = listOf(u2.userId, u4.userId), lastMessage = "Deal! I'll take the NBA ticket.", lastMessageTime = Date(now.time - 3 * day))
        val t4 = ChatThread(threadId = "t-004", participantIds = listOf(u3.userId, u4.userId), lastMessage = "Proof code sent. Enjoy the final!", lastMessageTime = Date(now.time - 39 * day))

        listOf(t1, t2, t3, t4).forEach { DatabaseManager.chat.insertThread(it) }

        val messages = listOf(
            Message("m-001", t1.threadId, u2.userId, "Hi Dogan! Is your Arctic Monkeys ticket still available?", Date(now.time - 87 * day)),
            Message("m-002", t1.threadId, u1.userId, "Yes it is! GA standing, great spot near the stage.", Date(now.time - 87 * day + 1800_000)),
            Message("m-003", t1.threadId, u2.userId, "Perfect, I'll buy it!", Date(now.time - 87 * day + 3600_000)),
            Message("m-004", t1.threadId, u1.userId, "Done! I'll send you the proof code after payment clears.", Date(now.time - 86 * day)),
            Message("m-005", t1.threadId, u2.userId, "Thanks! Enjoy the show!", Date(now.time - 84 * day)),

            Message("m-006", t2.threadId, u3.userId, "Hey Dogan, is the Man City vs Arsenal ticket still up?", Date(now.time - 2 * day)),
            Message("m-007", t2.threadId, u1.userId, "Yes, the seat is still available!", Date(now.time - 2 * day + 3600_000)),

            Message("m-008", t3.threadId, u4.userId, "Hey Keegan! Interested in the NBA Finals ticket.", Date(now.time - 4 * day)),
            Message("m-009", t3.threadId, u2.userId, "Sure! Lower bowl seat, amazing view of the court.", Date(now.time - 4 * day + 3600_000)),
            Message("m-010", t3.threadId, u4.userId, "Deal! I'll take the NBA ticket.", Date(now.time - 3 * day)),

            Message("m-011", t4.threadId, u4.userId, "Hi Phuc! Interested in the Champions League Final ticket.", Date(now.time - 46 * day)),
            Message("m-012", t4.threadId, u3.userId, "Great! Category 2 seat behind the goal, unforgettable experience.", Date(now.time - 46 * day + 3600_000)),
            Message("m-013", t4.threadId, u4.userId, "I'll take it! Sending payment now.", Date(now.time - 45 * day)),
            Message("m-014", t4.threadId, u3.userId, "Proof code sent. Enjoy the final!", Date(now.time - 39 * day))
        )
        messages.forEach { DatabaseManager.chat.insertMessage(it) }
    }
}
