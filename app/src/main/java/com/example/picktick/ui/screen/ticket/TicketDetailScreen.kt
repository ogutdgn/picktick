package com.example.picktick.ui.screen.ticket

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.picktick.model.UserRole
import com.example.picktick.PickTickBlue
import com.example.picktick.PickTickOrange
import com.example.picktick.ui.components.DetailRow
import com.example.picktick.navigation.AppState
import com.example.picktick.navigation.Screen
import com.example.picktick.service.AuthService
import com.example.picktick.service.CartService
import com.example.picktick.service.TicketService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(appState: AppState, ticketId: String) {
    val ticket = remember { TicketService.getListingById(ticketId) }
    val userId = appState.currentUser?.userId ?: ""
    val userRole = appState.currentUser?.role
    val isInCart = remember(ticketId) { CartService.isInCart(ticketId, userId) }
    var addedToCart by remember { mutableStateOf(isInCart) }
    var cartMessage by remember { mutableStateOf("") }

    if (ticket == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Ticket not found.")
        }
        return
    }

    val isOwnListing = ticket.sellerId == userId

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(ticket.title, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (userRole == UserRole.ADMIN) {
                            appState.navigate(Screen.AdminDashboard)
                        } else {
                            appState.userSelectedTab = 0
                            appState.navigate(Screen.UserDashboard)
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PickTickBlue)
            )
        },
        bottomBar = {
            if (userRole == UserRole.USER && !isOwnListing) {
                Surface(modifier = Modifier.fillMaxWidth(), color = Color.White, shadowElevation = 8.dp) {
                    Column(modifier = Modifier.padding(16.dp).navigationBarsPadding()) {
                        if (cartMessage.isNotEmpty()) {
                            Text(cartMessage, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
                        }
                        Button(
                            onClick = {
                                if (!addedToCart) {
                                    val success = CartService.addItem(ticket.id, userId)
                                    if (success) {
                                        addedToCart = true
                                        cartMessage = ""
                                    } else {
                                        cartMessage = "Already in cart or already purchased."
                                    }
                                } else {
                                    appState.navigate(Screen.Payment)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (addedToCart) Color(0xFF4CAF50) else PickTickOrange
                            )
                        ) {
                            if (addedToCart) {
                                Icon(Icons.Default.ShoppingCartCheckout, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Go to Checkout", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            } else {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Add to Shopping Cart", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            DetailRow("Event", ticket.eventName)
            DetailRow("Date / Time", "${ticket.date} @ ${ticket.time}")
            DetailRow("Location", ticket.location)
            DetailRow("Price", "$${ticket.price.toInt()}")
            DetailRow("Category", ticket.category)
            DetailRow("Seat", ticket.seat)

            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = PickTickOrange.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = PickTickOrange, modifier = Modifier.size(20.dp))
                    Column {
                        Text("Confirmation Code", fontSize = 12.sp, color = PickTickOrange, fontWeight = FontWeight.SemiBold)
                        Text("Will be provided to the buyer after purchase.", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text("Description", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(ticket.description, color = Color.Gray)

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            val seller = remember { AuthService.getUserById(ticket.sellerId) }
            if (seller != null && seller.userId != userId) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { appState.navigate(Screen.PublicProfile(seller.userId)) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Seller", fontSize = 12.sp, color = Color.Gray)
                        Text(seller.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PickTickBlue)
                        Text(
                            "${"%.1f".format(seller.rating)} ★  (${seller.reviewCount} reviews)",
                            fontSize = 13.sp,
                            color = PickTickOrange
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
                }
            }
        }
    }
}
