package com.example.front_end

import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.FilterChip
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.background
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.size
import android.app.DatePickerDialog
import java.util.Calendar
import androidx.compose.ui.platform.LocalContext
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.OutlinedButton
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerDashboard(onTicketClick: (TicketListing) -> Unit,
                   cartList: MutableList<TicketListing>) { // Pass navigation up
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf("All", "Music", "Sports", "Expo", "Theater", "Travel")
    var selectedCategory by remember { mutableStateOf("All") }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        // 1. STYLED SEARCH BAR
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            // The grey "Search" word that disappears when you type
            placeholder = {
                Text("Search", color = Color.Gray)
            },
            // The magnifying glass icon on the left
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = Color.Gray
                )
            },
            shape = RoundedCornerShape(25.dp), // Pill shape looks very modern
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = PickTickOrange, // Use the theme color
                unfocusedBorderColor = Color.Transparent, // Makes it look cleaner
                cursorColor = Color.Black
            ),
            singleLine = true
        )

        // 2. Horizontal Category Filter (Keep your existing code here)
        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = (category == selectedCategory),
                    onClick = { selectedCategory = category },
                    label = { Text(category) }
                )
            }
        }
        // 3. Price Range Bar
        PriceRangeBar()
        // 4. Date Range Filter
        DateRangeFilter()

        // 5. The Results List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            val filteredList = fakeListings.filter {
                (selectedCategory == "All" || it.category == selectedCategory) &&
                        (it.title.contains(searchQuery, ignoreCase = true) || it.location.contains(searchQuery, ignoreCase = true))
            }

            items(filteredList) { listing ->
                // Now passing the actual navigation click
                TicketItem(listing = listing, onClick = { onTicketClick(listing) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceRangeBar() {
    var minPriceInput by remember { mutableStateOf("0") }
    var maxPriceInput by remember { mutableStateOf("9999") }

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
        Text(text = "Price Range", fontWeight = FontWeight.Bold, fontSize = 14.sp)

        // 3. THE INPUT BOXES (Min and Max side by side)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Min Price Box
            OutlinedTextField(
                value = minPriceInput,
                onValueChange = { if (it.all { char -> char.isDigit() }) minPriceInput = it },
                modifier = Modifier.weight(1f),
                label = { Text("Min $") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )

            Text("-", fontWeight = FontWeight.Bold)

            // Max Price Box
            OutlinedTextField(
                value = maxPriceInput,
                onValueChange = { if (it.all { char -> char.isDigit() }) maxPriceInput = it },
                modifier = Modifier.weight(1f),
                label = { Text("Max $") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}
@Composable
fun DateRangeFilter() {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // States to hold the selected dates as strings
    var startDate by remember { mutableStateOf("Start Date") }
    var endDate by remember { mutableStateOf("End Date") }

    // Helper function to show the system DatePicker
    fun showDatePicker(onDateSelected: (String) -> Unit) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                onDateSelected("$dayOfMonth/${month + 1}/$year")
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
        Text(text = "Date", fontWeight = FontWeight.Bold, fontSize = 14.sp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Start Date Button
            OutlinedButton(
                onClick = { showDatePicker { startDate = it } },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(text = startDate, color = Color.DarkGray, fontSize = 12.sp)
            }

            // End Date Button
            OutlinedButton(
                onClick = { showDatePicker { endDate = it } },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(text = endDate, color = Color.DarkGray, fontSize = 12.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShoppingCartScreen(
    cartTickets: List<TicketListing>,
    onBack: () -> Unit,
    onCheckout: () -> Unit
) {
    // State for those small blue boxes you wanted
    val selectedTicketIds = remember { mutableStateListOf<String>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Cart (${cartTickets.size})", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PickTickBlue)
            )
        },
                bottomBar = {
            if (cartTickets.isNotEmpty()) {
                Button(
                    onClick = onCheckout,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PickTickBlue)
                ) {
                    Text("Proceed to Payment", color = Color.White)

                }
            }
        }


    ) { padding ->
        if (cartTickets.isEmpty()) {
            // THE "EMPTY" FACE
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your cart is empty!!", color = Color.Gray)
            }
        } else {
            // THE "LIST" FACE
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
                items(cartTickets) { ticket ->
                    Box(modifier = Modifier.padding(vertical = 8.dp)) {
                        // Reusing your dashboard's ticket item
                        TicketItem(listing = ticket, onClick = {})

                        // Small Blue Box for selecting (Top Right)
                        val isSelected = ticket.id in selectedTicketIds
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(24.dp)
                                .background(
                                    if (isSelected) PickTickBlue else Color.White,
                                    RoundedCornerShape(4.dp)
                                )
                                .border(1.dp, PickTickBlue, RoundedCornerShape(4.dp))
                                .clickable {
                                    if (isSelected) selectedTicketIds.remove(ticket.id)
                                    else selectedTicketIds.add(ticket.id)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) Icon(Icons.Default.Check, "", tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
