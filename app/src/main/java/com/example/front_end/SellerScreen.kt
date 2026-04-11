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
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.MoreVert

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboard(onTicketClick: (TicketListing) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf("All", "Music", "Sports", "Expo", "Theater", "Travel")
    var selectedCategory by remember { mutableStateOf("All") }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        // 1. SEARCH BAR
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            placeholder = { Text("Search Marketplace", color = Color.Gray) },
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

        // 2. CATEGORY FILTER
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

        // 3. Reuse your existing components
        PriceRangeBar()
        DateRangeFilter()

        // 4. THE RESULTS LIST
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            val filteredList = fakeListings.filter {
                (selectedCategory == "All" || it.category == selectedCategory) &&
                        (it.title.contains(searchQuery, ignoreCase = true))
            }

            items(filteredList) { listing ->
                TicketItem(listing = listing, onClick = { onTicketClick(listing) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun SalesDashboardScreen(onListNewTicket: () -> Unit, onModifyTicket: (TicketListing) -> Unit) {
    // Fake data representing ONLY the tickets this seller has listed
    var showConfirmDialog by remember { mutableStateOf(false) }
    var selectedForModify by remember { mutableStateOf<TicketListing?>(null) }
    val myListings = remember {
        mutableStateListOf(
            TicketListing(
                id = "1001",
                title = "My Music Fest",
                eventName = "Summer Jam 2026",
                date = "Aug 15",
                time = "06:00 PM",
                location = "Austin, TX",
                price = "$120",
                category = "Music",
                seat = "General Admission",
                description = "Selling my extra ticket for the summer jam."
            )
        )
    }
    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Modify Listing") },
            text = { Text("Do you want to modify this ticket?") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    selectedForModify?.let { onModifyTicket(it) }
                }) { Text("Yes", color = PickTickBlue, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text("No", color = Color.Red)
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            Button(
                onClick = onListNewTicket,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PickTickOrange)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("List New Ticket", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            Text(
                text = "My Current Listings",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Bold
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                items(myListings) { listing ->
                    SellerTicketItem(
                        listing = listing,
                        onModifyRequest = {
                            selectedForModify = listing
                            showConfirmDialog = true
                        }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketCreateScreen(onBack: () -> Unit) {
    // 1. State for all input fields
    var title by remember { mutableStateOf("") }
    var eventName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var seat by remember { mutableStateOf("N/A") }
    var description by remember { mutableStateOf("") }

    // Category State
    val categories = listOf("N/A", "Music", "Sports", "Expo", "Theater", "Travel")
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var expanded by remember { mutableStateOf(false) }

    // Date & Time States
    var dateText by remember { mutableStateOf("Select Date") }
    var timeText by remember { mutableStateOf("Select Time") }

    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState()
    var showTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create New Listing", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PickTickBlue)
        Text("Note:Ticket ID will be auto-generated by system", fontSize = 12.sp, color = Color.Gray)

        Spacer(modifier = Modifier.height(20.dp))

        // Title Input
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Listing Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Event Name Input
        OutlinedTextField(
            value = eventName,
            onValueChange = { eventName = it },
            label = { Text("Event Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Date Picker Field
        OutlinedTextField(
            value = dateText,
            onValueChange = {},
            readOnly = true,
            label = { Text("Date") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Time Picker Field
        OutlinedTextField(
            value = timeText,
            onValueChange = {},
            readOnly = true,
            label = { Text("Time") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { showTimePicker = true }) {
                    Icon(Icons.Default.AccessTime, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Location Input
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            selectedCategory = category
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Seat Input
        OutlinedTextField(
            value = seat,
            onValueChange = { seat = it },
            label = { Text("Seat No") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Description Input (Multi-line)
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(32.dp))

        // THE CREATE LISTING BUTTON
        Button(
            onClick = { /* Back-end functionality later */ },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
            border = BorderStroke(2.dp, Color.Black)
        ) {
            Text("Create Listing", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        }

        TextButton(onClick = onBack) {
            Text("Cancel", color = Color.Gray)
        }
    }

    // --- DIALOGS FOR DATE AND TIME ---

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val date = datePickerState.selectedDateMillis
                    if (date != null) {
                        dateText = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault()).format(java.util.Date(date))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            }
        ) { DatePicker(state = datePickerState) }
    }

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    timeText = "${timePickerState.hour}:${String.format("%02d", timePickerState.minute)}"
                    showTimePicker = false
                }) { Text("OK") }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }
}
@Composable
fun SellerTicketItem(listing: TicketListing, onModifyRequest: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = listing.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = listing.eventName, color = Color.Gray, fontSize = 14.sp)
                Text(text = "${listing.date} | ${listing.price}", color = PickTickOrange, fontWeight = FontWeight.SemiBold)
            }

            // The 3-vertical dots button
            IconButton(onClick = onModifyRequest) {
                Icon(Icons.Default.MoreVert, contentDescription = "Options")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyListingScreen(ticket: TicketListing, onBack: () -> Unit) {
    // 1. State for all input fields
    var title by remember { mutableStateOf(ticket.title) }
    var eventName by remember { mutableStateOf(ticket.eventName) }
    var location by remember { mutableStateOf(ticket.location) }
    var seat by remember { mutableStateOf(ticket.seat) }
    var description by remember { mutableStateOf(ticket.description) }

    // Category State
    val categories = listOf(ticket.category)
    var selectedCategory by remember { mutableStateOf(ticket.category) }
    var expanded by remember { mutableStateOf(false) }

    // Date & Time States
    var dateText by remember { mutableStateOf(ticket.date) }
    var timeText by remember { mutableStateOf(ticket.time) }

    val datePickerState = rememberDatePickerState()
    var showDatePicker by remember { mutableStateOf(false) }

    val timePickerState = rememberTimePickerState()
    var showTimePicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Modify Listing no${ticket.id}", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PickTickOrange)


        Spacer(modifier = Modifier.height(20.dp))

        // Title Input
        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Listing Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Event Name Input
        OutlinedTextField(
            value = eventName,
            onValueChange = { eventName = it },
            label = { Text("Event Name") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Date Picker Field
        OutlinedTextField(
            value = dateText,
            onValueChange = {},
            readOnly = true,
            label = { Text("Date") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Time Picker Field
        OutlinedTextField(
            value = timeText,
            onValueChange = {},
            readOnly = true,
            label = { Text("Time") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = { showTimePicker = true }) {
                    Icon(Icons.Default.AccessTime, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Location Input
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Category Dropdown
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = selectedCategory,
                onValueChange = {},
                readOnly = true,
                label = { Text("Category") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            selectedCategory = category
                            expanded = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Seat Input
        OutlinedTextField(
            value = seat,
            onValueChange = { seat = it },
            label = { Text("Seat No") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Description Input (Multi-line)
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth().height(120.dp),
            maxLines = 4
        )

        Spacer(modifier = Modifier.height(32.dp))

        // THE UPDATE LISTING BUTTON
        Button(
            onClick = { /* Back-end update logic later */ },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Green), // Green Color
            border = BorderStroke(2.dp, Color.Black) // Black Border
        ) {
            Text(
                text = "Update Listing",
                color = Color.Blue, // Blue Words
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
        }
    }
}

