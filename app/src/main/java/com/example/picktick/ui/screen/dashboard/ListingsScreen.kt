package com.example.picktick.ui.screen.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.picktick.model.ListingStatus
import com.example.picktick.model.TicketListing
import com.example.picktick.PickTickBlue
import com.example.picktick.PickTickOrange
import com.example.picktick.navigation.AppState
import com.example.picktick.navigation.Screen
import com.example.picktick.service.TicketService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListingsScreen(appState: AppState) {
    val sellerId = appState.currentUser?.userId ?: ""
    var myListings by remember { mutableStateOf(TicketService.getListingsBySeller(sellerId)) }

    val filterOptions = listOf("All", "Published", "Unpublished", "Sold")
    var selectedFilter by remember { mutableStateOf("All") }
    var filterDropdownExpanded by remember { mutableStateOf(false) }
    var toggleTarget by remember { mutableStateOf<TicketListing?>(null) }

    if (toggleTarget != null) {
        val listing = toggleTarget!!
        val willPublish = listing.status == ListingStatus.UNPUBLISHED
        AlertDialog(
            onDismissRequest = { toggleTarget = null },
            title = { Text(if (willPublish) "Publish Listing" else "Unpublish Listing") },
            text = {
                Text(
                    if (willPublish) "Publish \"${listing.title}\"? It will be visible to all buyers."
                    else "Unpublish \"${listing.title}\"? It will be hidden from the marketplace."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    TicketService.togglePublish(listing.id)
                    myListings = TicketService.getListingsBySeller(sellerId)
                    toggleTarget = null
                }) {
                    Text(
                        if (willPublish) "Publish" else "Unpublish",
                        color = if (willPublish) Color(0xFF4CAF50) else Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = { TextButton(onClick = { toggleTarget = null }) { Text("Cancel") } }
        )
    }

    val filteredListings = myListings.filter {
        when (selectedFilter) {
            "Published" -> it.status == ListingStatus.ACTIVE
            "Unpublished" -> it.status == ListingStatus.UNPUBLISHED
            "Sold" -> it.status == ListingStatus.SOLD
            else -> true
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        Row(
            modifier = Modifier.fillMaxWidth().background(Color.White).padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box {
                TextButton(onClick = { filterDropdownExpanded = true }) {
                    Text(selectedFilter, fontWeight = FontWeight.SemiBold, color = Color(0xFF333333), fontSize = 14.sp)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF333333))
                }
                DropdownMenu(expanded = filterDropdownExpanded, onDismissRequest = { filterDropdownExpanded = false }) {
                    filterOptions.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    option,
                                    fontWeight = if (option == selectedFilter) FontWeight.Bold else FontWeight.Normal,
                                    color = if (option == selectedFilter) PickTickBlue else Color(0xFF333333)
                                )
                            },
                            onClick = { selectedFilter = option; filterDropdownExpanded = false }
                        )
                    }
                }
            }
            Button(
                onClick = { appState.navigate(Screen.CreateListing) },
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PickTickOrange),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(4.dp))
                Text("Create Listing", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }

        HorizontalDivider(color = Color(0xFFEEEEEE))

        if (filteredListings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No listings found.", color = Color.Gray, fontSize = 15.sp)
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items(filteredListings) { listing ->
                    SellerTicketItem(
                        listing = listing,
                        onEditClick = { appState.navigate(Screen.ModifyListing(listing.id)) },
                        onTogglePublish = { toggleTarget = listing }
                    )
                }
            }
        }
    }
}

@Composable
fun SellerTicketItem(listing: TicketListing, onEditClick: () -> Unit, onTogglePublish: () -> Unit) {
    val isPublished = listing.status == ListingStatus.ACTIVE
    val isSold = listing.status == ListingStatus.SOLD

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(modifier = Modifier.padding(14.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(listing.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(listing.eventName, color = Color.Gray, fontSize = 13.sp)
                Text("${listing.date}  •  $${listing.price.toInt()}", color = PickTickOrange, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = when (listing.status) {
                        ListingStatus.ACTIVE -> Color(0xFF4CAF50).copy(alpha = 0.12f)
                        ListingStatus.UNPUBLISHED -> Color.Gray.copy(alpha = 0.15f)
                        ListingStatus.SOLD -> Color.Red.copy(alpha = 0.12f)
                    }
                ) {
                    Text(
                        text = when (listing.status) {
                            ListingStatus.ACTIVE -> "Published"
                            ListingStatus.UNPUBLISHED -> "Unpublished"
                            ListingStatus.SOLD -> "Sold"
                        },
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = when (listing.status) {
                            ListingStatus.ACTIVE -> Color(0xFF4CAF50)
                            ListingStatus.UNPUBLISHED -> Color.Gray
                            ListingStatus.SOLD -> Color.Red
                        },
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(4.dp)) {
                if (!isSold) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isPublished) "Published" else "Publish",
                            fontSize = 12.sp,
                            color = if (isPublished) Color(0xFF4CAF50) else Color.Gray,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Switch(
                            checked = isPublished,
                            onCheckedChange = { onTogglePublish() },
                            modifier = Modifier.height(24.dp),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = Color(0xFF4CAF50),
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = Color.LightGray
                            )
                        )
                    }
                }
                OutlinedButton(
                    onClick = onEditClick,
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp), tint = PickTickBlue)
                    Spacer(Modifier.width(4.dp))
                    Text("Edit", fontSize = 12.sp, color = PickTickBlue)
                }
            }
        }
    }
}
