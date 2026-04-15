package com.example.picktick

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
import com.example.picktick.data.DatabaseManager
import com.example.picktick.navigation.AppState
import com.example.picktick.navigation.Screen
import com.example.picktick.ui.screen.auth.LoginScreen
import com.example.picktick.ui.screen.auth.PasswordResetScreen
import com.example.picktick.ui.screen.auth.RegisterScreen
import com.example.picktick.ui.screen.admin.AdminDashboard
import com.example.picktick.ui.screen.chat.ChatThreadScreen
import com.example.picktick.ui.screen.dashboard.DashboardScreen
import com.example.picktick.ui.screen.dashboard.PublicProfileScreen
import com.example.picktick.ui.screen.payment.PaymentScreen
import com.example.picktick.ui.screen.ticket.CreateListingScreen
import com.example.picktick.ui.screen.ticket.ModifyListingScreen
import com.example.picktick.ui.screen.ticket.TicketDetailScreen
import kotlinx.coroutines.delay

val PickTickBlue = Color(0xFF0074BD)
val PickTickOrange = Color(0xFFFF9800)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DatabaseManager.init(this)
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
        is Screen.UserDashboard -> DashboardScreen(appState)
        is Screen.AdminDashboard -> AdminDashboard(appState)
        is Screen.TicketDetail -> TicketDetailScreen(appState, screen.ticketId)
        is Screen.Payment -> PaymentScreen(appState)
        is Screen.CreateListing -> CreateListingScreen(appState)
        is Screen.ModifyListing -> ModifyListingScreen(appState, screen.ticketId)
        is Screen.ChatThread -> ChatThreadScreen(appState, screen.threadId)
        is Screen.PublicProfile -> PublicProfileScreen(appState, screen.userId)
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
