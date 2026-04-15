package com.example.picktick.ui.screen.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import com.example.picktick.model.User
import com.example.picktick.PickTickBlue
import com.example.picktick.PickTickOrange
import com.example.picktick.navigation.AppState
import com.example.picktick.ui.screen.chat.ChatListScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboard(appState: AppState) {
    val tab = appState.adminSelectedTab
    var selectedUser by remember { mutableStateOf<User?>(null) }
    var showDropdown by remember { mutableStateOf(false) }

    if (selectedUser != null) {
        AdminUserDetail(
            user = selectedUser!!,
            appState = appState,
            onBack = { selectedUser = null },
            onUserUpdated = { selectedUser = null }
        )
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Box(contentAlignment = Alignment.CenterStart) {
                        Text(
                            text = "🎫  PickTick",
                            style = TextStyle(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = PickTickOrange,
                                drawStyle = Stroke(miter = 10f, width = 5f)
                            )
                        )
                        Text(
                            text = "🎫  PickTick",
                            style = TextStyle(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                },
                actions = {
                    Box {
                        TextButton(onClick = { showDropdown = true }) {
                            Text("Admin", color = Color.White, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = showDropdown,
                            onDismissRequest = { showDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Log Out", color = Color.Red, fontWeight = FontWeight.SemiBold) },
                                onClick = { showDropdown = false; appState.logout() },
                                leadingIcon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red) }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PickTickBlue)
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 6.dp) {
                NavigationBarItem(
                    selected = tab == 0,
                    onClick = { appState.adminSelectedTab = 0 },
                    icon = { Icon(Icons.Default.People, contentDescription = null) },
                    label = { Text("Users") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PickTickBlue,
                        selectedTextColor = PickTickBlue,
                        indicatorColor = PickTickBlue.copy(alpha = 0.12f)
                    )
                )
                NavigationBarItem(
                    selected = tab == 1,
                    onClick = { appState.adminSelectedTab = 1 },
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    label = { Text("Listings") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PickTickBlue,
                        selectedTextColor = PickTickBlue,
                        indicatorColor = PickTickBlue.copy(alpha = 0.12f)
                    )
                )
                NavigationBarItem(
                    selected = tab == 2,
                    onClick = { appState.adminSelectedTab = 2 },
                    icon = { Icon(Icons.Default.Chat, contentDescription = null) },
                    label = { Text("Messages") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PickTickBlue,
                        selectedTextColor = PickTickBlue,
                        indicatorColor = PickTickBlue.copy(alpha = 0.12f)
                    )
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF5F5F5))) {
            when (tab) {
                0 -> AdminUsersTab(onUserClick = { selectedUser = it })
                1 -> AdminListingsTab()
                2 -> ChatListScreen(appState)
            }
        }
    }
}
