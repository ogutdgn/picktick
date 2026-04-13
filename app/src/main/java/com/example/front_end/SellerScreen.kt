package com.example.front_end

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.front_end.model.ListingStatus
import com.example.front_end.model.TicketListing
import com.example.front_end.navigation.AppState
import com.example.front_end.navigation.Screen
import com.example.front_end.service.TicketService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboardShell(appState: AppState) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("PickTick Menu", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
                HorizontalDivider()

                NavigationDrawerItem(
                    label = { Text("Marketplace") },
                    selected = true,
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    onClick = { scope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    label = { Text("Sales Dashboard") },
                    selected = false,
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        appState.navigate(Screen.SalesDashboard)
                    }
                )
                NavigationDrawerItem(
                    label = { Text("User Profile") },
                    selected = false,
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        appState.navigate(Screen.Profile)
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Chatbox") },
                    selected = false,
                    icon = { Icon(Icons.Default.Chat, contentDescription = null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        appState.navigate(Screen.ChatList)
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                NavigationDrawerItem(
                    label = { Text("Log Out") },
                    selected = false,
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                    onClick = {
                        showLogoutDialog = true
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                Surface(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.1f), color = PickTickBlue) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp)
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                        Box(modifier = Modifier.align(Alignment.Center), contentAlignment = Alignment.Center) {
                            Text(
                                text = "🎫  PickTick  🎫",
                                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PickTickOrange, drawStyle = Stroke(miter = 10f, width = 5f))
                            )
                            Text(
                                text = "🎫  PickTick  🎫",
                                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize().background(Color(0xFFE0E0E0))) {
                SellerMarketplace(appState)
            }
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Log Out") },
                text = { Text("Are you sure you want to log out?") },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutDialog = false
                        appState.logout()
                    }) { Text("Yes", color = Color.Red) }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) { Text("No") }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerMarketplace(appState: AppState) {
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf("All", "Music", "Sports", "Expo", "Theater", "Travel")
    var selectedCategory by remember { mutableStateOf("All") }
    val listings = remember { TicketService.getAllActiveListings() }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
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

        PriceRangeBar()
        DateRangeFilter()

        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
            val filtered = listings.filter {
                (selectedCategory == "All" || it.category == selectedCategory) &&
                        it.title.contains(searchQuery, ignoreCase = true)
            }
            items(filtered) { listing ->
                TicketItem(listing = listing, onClick = { appState.navigate(Screen.TicketDetail(listing.id)) })
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SalesDashboardScreen(appState: AppState) {
    val sellerId = appState.currentUser?.userId ?: ""
    var myListings by remember { mutableStateOf(TicketService.getListingsBySeller(sellerId)) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var selectedForModify by remember { mutableStateOf<TicketListing?>(null) }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text("Modify Listing") },
            text = { Text("Do you want to modify this ticket?") },
            confirmButton = {
                TextButton(onClick = {
                    showConfirmDialog = false
                    selectedForModify?.let { appState.navigate(Screen.ModifyListing(it.id)) }
                }) { Text("Yes", color = PickTickBlue, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) { Text("No", color = Color.Red) }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sales Dashboard", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { appState.navigate(Screen.SellerDashboard) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PickTickBlue)
            )
        },
        bottomBar = {
            Button(
                onClick = { appState.navigate(Screen.CreateListing) },
                modifier = Modifier.fillMaxWidth().padding(16.dp).height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PickTickOrange)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                Spacer(Modifier.width(8.dp))
                Text("List New Ticket", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF5F5F5))) {
            Text(
                "My Current Listings",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.padding(16.dp),
                fontWeight = FontWeight.Bold
            )
            if (myListings.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No listings yet. Create one!", color = Color.Gray)
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)) {
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
                    IconButton(onClick = { appState.navigate(Screen.SalesDashboard) }) {
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

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(errorMessage, color = Color.Red, fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

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
                                proofCode = proofCode.trim()
                            )
                            appState.navigate(Screen.SalesDashboard)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PickTickBlue)
            ) {
                Text("Create Listing", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            TextButton(onClick = { appState.navigate(Screen.SalesDashboard) }) {
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
                title = { Text("Modify Listing #${ticket.id}", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { appState.navigate(Screen.SalesDashboard) }) {
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
                    appState.navigate(Screen.SalesDashboard)
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
            ) {
                Text("Update Listing", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }

            TextButton(onClick = { appState.navigate(Screen.SalesDashboard) }) {
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
                Text(listing.title, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(listing.eventName, color = Color.Gray, fontSize = 14.sp)
                Text(
                    "${listing.date} | $${listing.price.toInt()}",
                    color = PickTickOrange,
                    fontWeight = FontWeight.SemiBold
                )
                if (listing.status == ListingStatus.SOLD) {
                    Text("SOLD", color = Color.Red, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
            IconButton(onClick = onModifyRequest) {
                Icon(Icons.Default.MoreVert, contentDescription = "Options")
            }
        }
    }
}
