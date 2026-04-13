package com.example.front_end.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.front_end.navigation.AppState
import com.example.front_end.navigation.Screen

@Composable
fun PasswordResetScreen(appState: AppState) {
    var step by remember { mutableStateOf(1) }
    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Reset Password", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PickTickBlue)
        Spacer(modifier = Modifier.height(24.dp))

        when (step) {
            1 -> {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Enter your email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { if (email.isNotBlank()) step = 2 },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PickTickBlue)
                ) {
                    Text("Send Code")
                }
            }
            2 -> {
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Enter Confirmation Code") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { if (code.isNotBlank()) step = 3 },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PickTickBlue)
                ) {
                    Text("Verify Code")
                }
            }
            3 -> {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { appState.navigate(Screen.Login) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PickTickOrange),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirm New Password")
                }
            }
        }

        TextButton(onClick = { appState.navigate(Screen.Login) }) {
            Text("Cancel", color = Color.Gray)
        }
    }
}
