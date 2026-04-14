package com.example.front_end.screen

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
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

@Composable
fun LoginScreen(appState: AppState) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Welcome to", fontSize = 20.sp, color = Color.Gray, fontWeight = FontWeight.Medium)

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "Pick",
                    style = TextStyle(fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, drawStyle = Stroke(miter = 10f, width = 5f), color = PickTickOrange)
                )
                Text(
                    text = "Pick",
                    style = TextStyle(fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, color = PickTickBlue)
                )
            }
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "Tick",
                    style = TextStyle(fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, drawStyle = Stroke(miter = 10f, width = 5f), color = PickTickBlue)
                )
                Text(
                    text = "Tick",
                    style = TextStyle(fontSize = 48.sp, fontWeight = FontWeight.ExtraBold, color = PickTickOrange)
                )
            }
        }

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; errorMessage = "" },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; errorMessage = "" },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = { appState.navigate(Screen.Register) }) {
                Text("Sign up", color = PickTickBlue, fontWeight = FontWeight.Bold)
            }
            TextButton(onClick = { appState.navigate(Screen.PasswordReset) }) {
                Text("Forgot your password?", color = PickTickOrange, fontSize = 12.sp)
            }
        }

        if (errorMessage.isNotEmpty()) {
            Text(errorMessage, color = Color.Red, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val trimmedEmail = email.trim()
                val user = AuthService.login(trimmedEmail, password)
                if (user == null) {
                    errorMessage = when {
                        AuthService.isBanned(trimmedEmail) -> "This account has been banned. Please contact support."
                        else -> "Invalid email or password."
                    }
                } else {
                    appState.currentUser = user
                    when (user.role) {
                        UserRole.USER -> appState.navigate(Screen.UserDashboard)
                        UserRole.ADMIN -> appState.navigate(Screen.AdminDashboard)
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = PickTickBlue),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Login", color = Color.White)
        }
    }
}
