package com.example.front_end

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.front_end.model.ListingStatus
import com.example.front_end.model.TicketListing
import com.example.front_end.navigation.AppState
import com.example.front_end.navigation.Screen
import com.example.front_end.service.TicketService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyListingsContent(appState: AppState) {
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
            dismissButton = {
                TextButton(onClick = { toggleTarget = null }) { Text("Cancel") }
            }
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
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box {
                TextButton(onClick = { filterDropdownExpanded = true }) {
                    Text(selectedFilter, fontWeight = FontWeight.SemiBold, color = Color(0xFF333333), fontSize = 14.sp)
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color(0xFF333333))
                }
                DropdownMenu(
                    expanded = filterDropdownExpanded,
                    onDismissRequest = { filterDropdownExpanded = false }
                ) {
                    filterOptions.forEach { option ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    option,
                                    fontWeight = if (option == selectedFilter) FontWeight.Bold else FontWeight.Normal,
                                    color = if (option == selectedFilter) PickTickBlue else Color(0xFF333333)
                                )
                            },
                            onClick = {
                                selectedFilter = option
                                filterDropdownExpanded = false
                            }
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
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
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
fun SellerTicketItem(
    listing: TicketListing,
    onEditClick: () -> Unit,
    onTogglePublish: () -> Unit
) {
    val isPublished = listing.status == ListingStatus.ACTIVE
    val isSold = listing.status == ListingStatus.SOLD

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
                Text(listing.eventName, color = Color.Gray, fontSize = 13.sp)
                Text(
                    "${listing.date}  •  $${listing.price.toInt()}",
                    color = PickTickOrange,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
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

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateListingScreen(appState: AppState) {
    val sellerId = appState.currentUser?.userId ?: ""
    var title by remember { mutableStateOf("") }
    var eventName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var seat by remember { mutableStateOf("N/A") }
    var description by remember { mutableStateOf("") }
    var priceInput by remember { mutableStateOf("") }
    var proofCode by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var publishNow by remember { mutableStateOf(true) }

    val categories = listOf("Music", "Sports", "Expo", "Theater", "Travel")
    var selectedCategory by remember { mutableStateOf(categories[0]) }
    var expanded by remember { mutableStateOf(false) }

    var dateText by remember { mutableStateOf("Select Date") }
    var timeText by remember { mutableStateOf("Select Time") }
    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Create New Listing", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {
                        appState.userSelectedTab = 1
                        appState.navigate(Screen.UserDashboard)
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PickTickBlue)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().background(Color.White)
                .verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(value = title, onValueChange = { title = it; errorMessage = "" }, label = { Text("Listing Title") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(value = eventName, onValueChange = { eventName = it }, label = { Text("Event Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

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

            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
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
                        DropdownMenuItem(text = { Text(category) }, onClick = { selectedCategory = category; expanded = false })
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(value = seat, onValueChange = { seat = it }, label = { Text("Seat No") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = priceInput,
                onValueChange = { if (it.all { c -> c.isDigit() || c == '.' }) priceInput = it },
                label = { Text("Price ($)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(value = proofCode, onValueChange = { proofCode = it }, label = { Text("Confirmation Code") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 4
            )
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Publish it now", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    Text(
                        if (publishNow) "Visible in marketplace immediately" else "Saved as unpublished draft",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                }
                Switch(
                    checked = publishNow,
                    onCheckedChange = { publishNow = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = Color(0xFF4CAF50),
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = Color.LightGray
                    )
                )
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage, color = Color.Red, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val price = priceInput.toDoubleOrNull()
                    when {
                        title.isBlank() -> errorMessage = "Title is required."
                        eventName.isBlank() -> errorMessage = "Event name is required."
                        dateText == "Select Date" -> errorMessage = "Select a date."
                        timeText == "Select Time" -> errorMessage = "Select a time."
                        location.isBlank() -> errorMessage = "Location is required."
                        price == null || price <= 0 -> errorMessage = "Enter a valid price."
                        proofCode.isBlank() -> errorMessage = "Confirmation code is required."
                        else -> {
                            TicketService.createListing(
                                title = title.trim(),
                                eventName = eventName.trim(),
                                date = dateText,
                                time = timeText,
                                location = location.trim(),
                                price = price,
                                category = selectedCategory,
                                seat = seat.trim(),
                                description = description.trim(),
                                sellerId = sellerId,
                                proofCode = proofCode.trim(),
                                published = publishNow
                            )
                            appState.userSelectedTab = 1
                            appState.navigate(Screen.UserDashboard)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PickTickBlue)
            ) {
                Text("Create Listing", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            TextButton(onClick = {
                appState.userSelectedTab = 1
                appState.navigate(Screen.UserDashboard)
            }) {
                Text("Cancel", color = Color.Gray)
            }
        }
    }

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModifyListingScreen(appState: AppState, ticketId: String) {
    val ticket = remember { TicketService.getListingById(ticketId) }

    if (ticket == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Listing not found.")
        }
        return
    }

    var title by remember { mutableStateOf(ticket.title) }
    var eventName by remember { mutableStateOf(ticket.eventName) }
    var location by remember { mutableStateOf(ticket.location) }
    var seat by remember { mutableStateOf(ticket.seat) }
    var description by remember { mutableStateOf(ticket.description) }
    var dateText by remember { mutableStateOf(ticket.date) }
    var timeText by remember { mutableStateOf(ticket.time) }

    val categories = listOf("Music", "Sports", "Expo", "Theater", "Travel")
    var selectedCategory by remember { mutableStateOf(ticket.category) }
    var expanded by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState()
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Modify Listing", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {
                        appState.userSelectedTab = 1
                        appState.navigate(Screen.UserDashboard)
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PickTickOrange)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().background(Color.White)
                .verticalScroll(rememberScrollState()).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Listing Title") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(value = eventName, onValueChange = { eventName = it }, label = { Text("Event Name") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

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

            OutlinedTextField(value = location, onValueChange = { location = it }, label = { Text("Location") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

            ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
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
                        DropdownMenuItem(text = { Text(category) }, onClick = { selectedCategory = category; expanded = false })
                    }
                }
            }
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(value = seat, onValueChange = { seat = it }, label = { Text("Seat No") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth().height(120.dp),
                maxLines = 4
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = {
                    TicketService.updateListing(
                        ticket.copy(
                            title = title.trim(),
                            eventName = eventName.trim(),
                            date = dateText,
                            time = timeText,
                            location = location.trim(),
                            category = selectedCategory,
                            seat = seat.trim(),
                            description = description.trim()
                        )
                    )
                    appState.userSelectedTab = 1
                    appState.navigate(Screen.UserDashboard)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Update Listing", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            TextButton(onClick = {
                appState.userSelectedTab = 1
                appState.navigate(Screen.UserDashboard)
            }) {
                Text("Cancel", color = Color.Gray)
            }
        }
    }

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
