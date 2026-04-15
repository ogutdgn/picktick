package com.example.picktick.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.picktick.model.UserRole
import com.example.picktick.PickTickBlue
import com.example.picktick.PickTickOrange
import com.example.picktick.ui.components.ReviewDialog
import com.example.picktick.ui.components.TicketItem
import com.example.picktick.navigation.AppState
import com.example.picktick.navigation.Screen
import com.example.picktick.service.*

@Composable
fun ProfileScreen(appState: AppState) {
    val user = remember { AuthService.getUserById(appState.currentUser?.userId ?: "") }
    val liveRating = remember { if (user != null) ReviewService.getAverageRating(user.userId) else 0f }
    val liveReviewCount = remember { if (user != null) ReviewService.getReviewCount(user.userId) else 0 }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F8F8))) {
        TextButton(
            onClick = { appState.userSelectedTab = 0 },
            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp), tint = PickTickBlue)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Go Back", color = PickTickBlue, fontWeight = FontWeight.SemiBold)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(3.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(72.dp).clip(CircleShape).background(PickTickBlue), contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(44.dp), tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(user?.name ?: "Unknown", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text(user?.email ?: "", fontSize = 13.sp, color = Color.Gray)
                            Text(
                                user?.role?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "",
                                fontSize = 12.sp, color = PickTickBlue, fontWeight = FontWeight.SemiBold
                            )
                            if (liveReviewCount > 0) {
                                Text("${"%.1f".format(liveRating)} ★  ($liveReviewCount reviews)", fontSize = 13.sp, color = PickTickOrange)
                            }
                        }
                    }
                }
            }

            when (user?.role) {
                UserRole.USER -> {
                    item { Text("Purchase History", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333)) }
                    val purchases = OrderService.getOrdersByBuyer(user.userId)
                    if (purchases.isEmpty()) {
                        item { Text("No purchases yet.", color = Color.Gray) }
                    } else {
                        items(purchases) { order ->
                            val ticket = TicketService.getListingById(order.ticketId)
                            val seller = AuthService.getUserById(order.sellerId)
                            var showReviewDialog by remember { mutableStateOf(false) }
                            val alreadyReviewed = remember { ReviewService.hasUserReviewed(user.userId, order.sellerId) }

                            if (showReviewDialog) {
                                ReviewDialog(
                                    targetName = seller?.name ?: "Seller",
                                    onDismiss = { showReviewDialog = false },
                                    onSubmit = { rating ->
                                        ReviewService.submitReview(user.userId, order.sellerId, rating)
                                        showReviewDialog = false
                                    }
                                )
                            }

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(2.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(ticket?.title ?: order.ticketId, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Text("Seller: ${seller?.name ?: "Unknown"}", fontSize = 12.sp, color = Color.Gray)
                                        }
                                        Text("$${order.totalPrice.toInt()}", color = PickTickOrange, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                    if (!alreadyReviewed) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedButton(onClick = { showReviewDialog = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp), tint = PickTickOrange)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Rate Seller", color = PickTickOrange)
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("✓ Reviewed", fontSize = 12.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Sales History", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
                    }
                    val sales = OrderService.getOrdersBySeller(user.userId)
                    if (sales.isEmpty()) {
                        item { Text("No sales yet.", color = Color.Gray) }
                    } else {
                        items(sales) { order ->
                            val ticket = TicketService.getListingById(order.ticketId)
                            val buyer = AuthService.getUserById(order.buyerId)
                            var showReviewDialog by remember { mutableStateOf(false) }
                            val alreadyReviewed = remember { ReviewService.hasUserReviewed(user.userId, order.buyerId) }

                            if (showReviewDialog) {
                                ReviewDialog(
                                    targetName = buyer?.name ?: "Buyer",
                                    onDismiss = { showReviewDialog = false },
                                    onSubmit = { rating ->
                                        ReviewService.submitReview(user.userId, order.buyerId, rating)
                                        showReviewDialog = false
                                    }
                                )
                            }

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(2.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(ticket?.title ?: order.ticketId, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Text("Buyer: ${buyer?.name ?: "Unknown"}", fontSize = 12.sp, color = Color.Gray)
                                        }
                                        Text("$${order.totalPrice.toInt()}", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                    if (!alreadyReviewed) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedButton(onClick = { showReviewDialog = true }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp)) {
                                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp), tint = PickTickOrange)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Rate Buyer", color = PickTickOrange)
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("✓ Reviewed", fontSize = 12.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }
                UserRole.ADMIN -> {
                    item {
                        Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("Admin accounts do not have purchase or sales history.", color = Color.Gray, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicProfileScreen(appState: AppState, userId: String) {
    val user = remember { AuthService.getUserById(userId) }
    val reviews = remember(userId) { ReviewService.getByTarget(userId) }
    val listings = remember(userId) {
        TicketService.getListingsBySeller(userId).filter { it.status == com.example.picktick.model.ListingStatus.ACTIVE }
    }
    val currentUserId = appState.currentUser?.userId ?: ""

    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text("User not found.") }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(user.name, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (appState.currentUser?.role == UserRole.ADMIN) appState.navigate(Screen.AdminDashboard)
                        else appState.navigate(Screen.UserDashboard)
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PickTickBlue)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(shape = RoundedCornerShape(14.dp), elevation = CardDefaults.cardElevation(3.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(72.dp).clip(CircleShape).background(PickTickBlue), contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(44.dp), tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(user.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text(user.role.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 13.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("${"%.1f".format(user.rating)} ★  (${user.reviewCount} reviews)", fontSize = 14.sp, color = PickTickOrange, fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
            }

            if (currentUserId != userId) {
                item {
                    Button(
                        onClick = {
                            val thread = ChatService.getOrCreateThread(currentUserId, userId)
                            appState.navigate(Screen.ChatThread(thread.threadId))
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PickTickBlue)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Message", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            if (listings.isNotEmpty()) {
                item { Text("Active Listings", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333)) }
                items(listings) { listing ->
                    TicketItem(listing = listing, onClick = { appState.navigate(Screen.TicketDetail(listing.id)) })
                }
            }

            item { Text("Reviews", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333)) }

            if (reviews.isEmpty()) {
                item {
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) { Text("No reviews yet.", color = Color.Gray) }
                    }
                }
            } else {
                items(reviews) { review ->
                    val reviewer = remember { AuthService.getUserById(review.reviewerId) }
                    Card(shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(2.dp), colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
                        Row(modifier = Modifier.padding(14.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(reviewer?.name ?: "Unknown", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(review.rating.toInt()) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = PickTickOrange, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${"%.1f".format(review.rating)}", fontWeight = FontWeight.Bold, color = PickTickOrange)
                            }
                        }
                    }
                }
            }
        }
    }
}
