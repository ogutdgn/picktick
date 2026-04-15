package com.example.picktick.ui.screen.admin

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
import com.example.picktick.model.TicketListing
import com.example.picktick.PickTickBlue
import com.example.picktick.PickTickOrange
import com.example.picktick.ui.components.RoleBadge
import com.example.picktick.ui.components.StatusBadge
import com.example.picktick.service.AdminService

@Composable
fun AdminListingsTab() {
    var listings by remember { mutableStateOf(AdminService.getAllListings()) }
    var listingToDelete by remember { mutableStateOf<TicketListing?>(null) }

    if (listingToDelete != null) {
        AlertDialog(
            onDismissRequest = { listingToDelete = null },
            title = { Text("Delete Listing") },
            text = { Text("Delete \"${listingToDelete!!.title}\"? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    AdminService.deleteListing(listingToDelete!!.id)
                    listings = AdminService.getAllListings()
                    listingToDelete = null
                }) {
                    Text("Delete", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { listingToDelete = null }) { Text("Cancel") }
            }
        )
    }

    if (listings.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No listings found.", color = Color.Gray)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(listings) { listing ->
            AdminListingCard(listing = listing, onDelete = { listingToDelete = listing })
        }
    }
}

@Composable
fun AdminListingCard(listing: TicketListing, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(listing.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(listing.eventName, fontSize = 13.sp, color = Color.Gray)
                Text(
                    "${listing.date}  •  $${listing.price.toInt()}",
                    fontSize = 13.sp,
                    color = PickTickOrange,
                    fontWeight = FontWeight.SemiBold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                    StatusBadge(
                        listing.status.name,
                        when (listing.status.name) {
                            "ACTIVE" -> PickTickBlue
                            "SOLD" -> Color.Red
                            else -> Color.Gray
                        }
                    )
                    RoleBadge(listing.category)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }
    }
}
