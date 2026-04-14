package com.example.front_end.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.front_end.PickTickBlue
import com.example.front_end.PickTickOrange
import com.example.front_end.model.UserRole
import com.example.front_end.navigation.AppState
import com.example.front_end.navigation.Screen
import com.example.front_end.service.AuthService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(appState: AppState) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var showDatePicker by remember { mutableStateOf(false) }
    var dob by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val datePickerState = rememberDatePickerState()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create Account", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = PickTickBlue)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it; errorMessage = "" },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = dob,
            onValueChange = {},
            label = { Text("Date of Birth") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = null)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; errorMessage = "" },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage = "" },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(errorMessage, color = Color.Red, fontSize = 13.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                when {
                    fullName.isBlank() -> errorMessage = "Full name is required."
                    email.isBlank() -> errorMessage = "Email is required."
                    !email.contains("@") -> errorMessage = "Enter a valid email."
                    password.length < 6 -> errorMessage = "Password must be at least 6 characters."
                    else -> {
                        val success = AuthService.register(fullName.trim(), email.trim(), password, UserRole.USER)
                        if (success) appState.navigate(Screen.Login)
                        else errorMessage = "Email already registered."
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = PickTickOrange),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Sign Up", color = Color.White)
        }

        TextButton(onClick = { appState.navigate(Screen.Login) }) {
            Text("Already have an account? Login", color = Color.Gray)
        }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDate = datePickerState.selectedDateMillis
                    if (selectedDate != null) {
                        dob = java.text.SimpleDateFormat("MM/dd/yyyy", java.util.Locale.getDefault())
                            .format(java.util.Date(selectedDate))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
