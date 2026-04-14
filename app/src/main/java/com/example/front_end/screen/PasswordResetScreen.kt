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
import com.example.front_end.service.AuthService

@Composable
fun PasswordResetScreen(appState: AppState) {
    var step by remember { mutableStateOf(1) }
    var email by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Reset Password", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PickTickBlue)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            if (step == 1) "Enter the email address for your account." else "Create a new password for $email.",
            fontSize = 13.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(24.dp))

        when (step) {
            1 -> {
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it; errorMessage = "" },
                    label = { Text("Email Address") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorMessage, color = Color.Red, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val trimmed = email.trim()
                        when {
                            trimmed.isBlank() -> errorMessage = "Please enter your email address."
                            !AuthService.isEmailRegistered(trimmed) -> errorMessage = "No account found with this email."
                            else -> { step = 2; errorMessage = "" }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PickTickBlue)
                ) {
                    Text("Continue", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            2 -> {
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it; errorMessage = "" },
                    label = { Text("New Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it; errorMessage = "" },
                    label = { Text("Confirm New Password") },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )
                if (errorMessage.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(errorMessage, color = Color.Red, fontSize = 13.sp)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        when {
                            newPassword.length < 6 -> errorMessage = "Password must be at least 6 characters."
                            newPassword != confirmPassword -> errorMessage = "Passwords do not match."
                            else -> {
                                AuthService.changePassword(email.trim(), newPassword)
                                appState.navigate(Screen.Login)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PickTickOrange)
                ) {
                    Text("Save New Password", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        TextButton(onClick = { appState.navigate(Screen.Login) }) {
            Text("Back to Login", color = Color.Gray)
        }
    }
}
