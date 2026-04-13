package com.example.front_end

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.front_end.navigation.AppState
import com.example.front_end.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(appState: AppState) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Payment", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { appState.navigate(Screen.Cart) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PickTickBlue)
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Payment Screen",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = PickTickBlue
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    "Coming soon",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
