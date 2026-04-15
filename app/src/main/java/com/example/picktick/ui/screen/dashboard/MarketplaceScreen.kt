package com.example.picktick.ui.screen.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
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
import com.example.picktick.ui.components.DateRangeFilter
import com.example.picktick.ui.components.PriceRangeBar
import com.example.picktick.ui.components.TicketItem
import com.example.picktick.navigation.AppState
import com.example.picktick.navigation.Screen
import com.example.picktick.service.TicketService

@Composable
fun MarketplaceScreen(appState: AppState) {
    val userId = appState.currentUser?.userId ?: ""
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf("All", "Music", "Sports", "Expo", "Theater", "Travel")
    var selectedCategory by remember { mutableStateOf("All") }
    var filtersExpanded by remember { mutableStateOf(false) }

    var draftMinPrice by remember { mutableStateOf(appState.filterMinPrice) }
    var draftMaxPrice by remember { mutableStateOf(appState.filterMaxPrice) }
    var draftStartDate by remember { mutableStateOf(appState.filterStartDate) }
    var draftEndDate by remember { mutableStateOf(appState.filterEndDate) }

    val listings = remember { TicketService.getAllActiveListings().filter { it.sellerId != userId } }

    val minP = appState.filterMinPrice.toDoubleOrNull() ?: 0.0
    val maxP = appState.filterMaxPrice.toDoubleOrNull() ?: 9999.0

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            placeholder = { Text("Search", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            shape = RoundedCornerShape(25.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = PickTickOrange,
                unfocusedBorderColor = Color.Transparent
            ),
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LazyRow(modifier = Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { category ->
                    FilterChip(
                        selected = (category == selectedCategory),
                        onClick = { selectedCategory = category },
                        label = { Text(category) }
                    )
                }
            }
            TextButton(onClick = { filtersExpanded = !filtersExpanded }) {
                Text(
                    if (filtersExpanded) "▲" else "▼",
                    color = PickTickBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        AnimatedVisibility(visible = filtersExpanded) {
            Column {
                PriceRangeBar(
                    minPrice = draftMinPrice,
                    maxPrice = draftMaxPrice,
                    onMinChange = { draftMinPrice = it },
                    onMaxChange = { draftMaxPrice = it }
                )
                DateRangeFilter(
                    startDate = draftStartDate,
                    endDate = draftEndDate,
                    onStartChange = { draftStartDate = it },
                    onEndChange = { draftEndDate = it }
                )
                Button(
                    onClick = {
                        appState.filterMinPrice = draftMinPrice
                        appState.filterMaxPrice = draftMaxPrice
                        appState.filterStartDate = draftStartDate
                        appState.filterEndDate = draftEndDate
                        filtersExpanded = false
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp).height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PickTickBlue)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Save & Apply Filters", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        val filtered = listings.filter {
            (selectedCategory == "All" || it.category == selectedCategory) &&
            (it.title.contains(searchQuery, ignoreCase = true) || it.location.contains(searchQuery, ignoreCase = true)) &&
            it.price >= minP && it.price <= maxP
        }

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.SearchOff, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Text("Can't find any listings.", color = Color.Gray, fontSize = 15.sp)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                items(filtered) { listing ->
                    TicketItem(listing = listing, onClick = { appState.navigate(Screen.TicketDetail(listing.id)) })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}
