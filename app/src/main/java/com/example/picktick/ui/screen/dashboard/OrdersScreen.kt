package com.example.picktick.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.picktick.PickTickBlue
import com.example.picktick.PickTickOrange
import com.example.picktick.navigation.AppState
import com.example.picktick.service.OrderService
import com.example.picktick.service.TicketService
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun OrdersScreen(appState: AppState) {
    val userId = appState.currentUser?.userId ?: ""
    val orders = remember { OrderService.getOrdersByBuyer(userId) }
    var activeTab by remember { mutableStateOf(0) }
    val dateFmt = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    val todayStart = remember {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.time
    }

    val (activeOrders, pastOrders) = remember(orders) {
        orders.partition { order ->
            val ticket = TicketService.getListingById(order.ticketId)
            if (ticket != null) {
                try {
                    val eventDate = dateFmt.parse(ticket.date)
                    eventDate != null && !eventDate.before(todayStart)
                } catch (e: Exception) { true }
            } else true
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        TabRow(selectedTabIndex = activeTab, containerColor = Color.White, contentColor = PickTickBlue) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("Active", fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("Past", fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal) }
            )
        }

        val displayOrders = if (activeTab == 0) activeOrders else pastOrders

        if (displayOrders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.ConfirmationNumber, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Text(if (activeTab == 0) "No active tickets." else "No past tickets.", color = Color.Gray, fontSize = 15.sp)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(displayOrders) { order ->
                    val ticket = TicketService.getListingById(order.ticketId)
                    if (ticket != null) {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            elevation = CardDefaults.cardElevation(3.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(ticket.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PickTickBlue)
                                Text(ticket.eventName, fontSize = 13.sp, color = Color.Gray)
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Text("📅 ${ticket.date}", fontSize = 13.sp)
                                    Text("🕐 ${ticket.time}", fontSize = 13.sp)
                                }
                                Text("📍 ${ticket.location}", fontSize = 13.sp)
                                Text("💺 ${ticket.seat}", fontSize = 13.sp)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Confirmation Code", fontSize = 11.sp, color = Color.Gray)
                                        Text(ticket.proofCode, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PickTickOrange)
                                    }
                                    Text("$${"%.2f".format(order.totalPrice)}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PickTickBlue)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
