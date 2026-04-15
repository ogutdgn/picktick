package com.example.picktick.ui.screen.ticket

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.picktick.PickTickOrange
import com.example.picktick.navigation.AppState
import com.example.picktick.navigation.Screen
import com.example.picktick.service.TicketService

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
    var proofCode by remember { mutableStateOf(ticket.proofCode) }

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
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(value = proofCode, onValueChange = { proofCode = it }, label = { Text("Confirmation Number") }, modifier = Modifier.fillMaxWidth())

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
                            description = description.trim(),
                            proofCode = proofCode.trim()
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
