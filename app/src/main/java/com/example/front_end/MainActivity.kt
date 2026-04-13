package com.example.front_end

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.Text
import com.example.front_end.navigation.AppState
import com.example.front_end.navigation.Screen
import com.example.front_end.screen.LoginScreen
import com.example.front_end.screen.PasswordResetScreen
import com.example.front_end.screen.RegisterScreen
import kotlinx.coroutines.delay

val PickTickBlue = Color(0xFF0074BD)
val PickTickOrange = Color(0xFFFF9800)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val appState = remember { AppState() }
            AppNavigation(appState)
        }
    }
}

@Composable
fun AppNavigation(appState: AppState) {
    when (val screen = appState.currentScreen) {
        is Screen.Splash -> SplashScreen(appState)
        is Screen.Login -> LoginScreen(appState)
        is Screen.Register -> RegisterScreen(appState)
        is Screen.PasswordReset -> PasswordResetScreen(appState)
        is Screen.BuyerDashboard -> BuyerDashboardShell(appState)
        is Screen.SellerDashboard -> SellerDashboardShell(appState)
        is Screen.AdminDashboard -> AdminDashboardShell(appState)
        is Screen.TicketDetail -> TicketDetailScreen(appState, screen.ticketId)
        is Screen.Cart -> CartScreen(appState)
        is Screen.Payment -> PaymentScreen(appState)
        is Screen.Profile -> ProfileScreen(appState)
        is Screen.ChatList -> ChatListScreen(appState)
        is Screen.ChatThread -> ChatThreadScreen(appState, screen.threadId)
        is Screen.SalesDashboard -> SalesDashboardScreen(appState)
        is Screen.CreateListing -> CreateListingScreen(appState)
        is Screen.ModifyListing -> ModifyListingScreen(appState, screen.ticketId)
    }
}

@Composable
fun SplashScreen(appState: AppState) {
    LaunchedEffect(Unit) {
        delay(1000)
        appState.navigate(Screen.Login)
    }
    Box(
        modifier = Modifier.fillMaxSize().background(PickTickBlue),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = "🎫", fontSize = 40.sp, modifier = Modifier.rotate(-15f))
            Spacer(modifier = Modifier.width(16.dp))
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "PickTick",
                    style = TextStyle(
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                        drawStyle = Stroke(miter = 10f, width = 8f),
                        color = Color.White
                    )
                )
                Text(
                    text = "PickTick",
                    style = TextStyle(
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Bold,
                        color = PickTickOrange
                    )
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Text(text = "🎫", fontSize = 40.sp, modifier = Modifier.rotate(15f))
        }
    }
}
