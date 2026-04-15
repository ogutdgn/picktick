package com.example.picktick.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
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
import com.example.picktick.navigation.Screen
import com.example.picktick.service.CartService

@Composable
fun CartScreen(appState: AppState) {
    val userId = appState.currentUser?.userId ?: ""
    var cartListings by remember { mutableStateOf(CartService.getCartListings(userId)) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        Text(
            "My Cart (${cartListings.size})",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        if (cartListings.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Your cart is empty!", color = Color.Gray, fontSize = 16.sp)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                items(cartListings) { ticket ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(3.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(ticket.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PickTickBlue)
                                Text(ticket.eventName, fontSize = 12.sp, color = Color.Gray)
                                Text("${ticket.date}  •  ${ticket.time}", fontSize = 12.sp, color = Color.Gray)
                                Text(ticket.location, fontSize = 12.sp, color = Color.Gray)
                                Text("$${ticket.price.toInt()}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PickTickOrange)
                            }
                            IconButton(onClick = {
                                CartService.removeItem(ticket.id, userId)
                                cartListings = CartService.getCartListings(userId)
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
                            }
                        }
                    }
                }
            }

            Surface(modifier = Modifier.fillMaxWidth(), color = Color.White, shadowElevation = 6.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total", fontSize = 16.sp, color = Color.Gray)
                        Text(
                            "$${"%.2f".format(cartListings.sumOf { it.price })}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PickTickBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { appState.navigate(Screen.Payment) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PickTickBlue)
                    ) {
                        Text("Proceed to Payment", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
