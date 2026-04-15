package com.example.picktick.ui.screen.dashboard

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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.picktick.PickTickBlue
import com.example.picktick.PickTickOrange
import com.example.picktick.navigation.AppState
import com.example.picktick.ui.screen.chat.ChatListScreen
import com.example.picktick.service.CartService

val navItemColors
    @Composable get() = NavigationBarItemDefaults.colors(
        selectedIconColor = PickTickBlue,
        selectedTextColor = PickTickBlue,
        unselectedIconColor = Color.Gray,
        unselectedTextColor = Color.Gray,
        indicatorColor = PickTickBlue.copy(alpha = 0.12f)
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(appState: AppState) {
    val tab = appState.userSelectedTab
    var showDropdown by remember { mutableStateOf(false) }
    val userId = appState.currentUser?.userId ?: ""
    val cartCount = remember(tab) { CartService.getCart(userId).size }

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
                            Text(
                                text = appState.currentUser?.name ?: "",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
                        }
                        DropdownMenu(
                            expanded = showDropdown,
                            onDismissRequest = { showDropdown = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Profile") },
                                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                                onClick = {
                                    showDropdown = false
                                    appState.userSelectedTab = -1
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Log Out", color = Color.Red) },
                                leadingIcon = { Icon(Icons.Default.ExitToApp, contentDescription = null, tint = Color.Red) },
                                onClick = {
                                    showDropdown = false
                                    appState.logout()
                                }
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
                    onClick = { appState.userSelectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Marketplace") },
                    label = { Text("Marketplace", fontSize = 10.sp) },
                    colors = navItemColors
                )
                NavigationBarItem(
                    selected = tab == 1,
                    onClick = { appState.userSelectedTab = 1 },
                    icon = { Icon(Icons.Default.List, contentDescription = "My Listings") },
                    label = { Text("My Listings", fontSize = 10.sp) },
                    colors = navItemColors
                )
                NavigationBarItem(
                    selected = tab == 2,
                    onClick = { appState.userSelectedTab = 2 },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (cartCount > 0) {
                                    Badge(containerColor = PickTickOrange) {
                                        Text("$cartCount", color = Color.White, fontSize = 10.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                    },
                    label = { Text("Cart", fontSize = 10.sp) },
                    colors = navItemColors
                )
                NavigationBarItem(
                    selected = tab == 3,
                    onClick = { appState.userSelectedTab = 3 },
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Messages") },
                    label = { Text("Messages", fontSize = 10.sp) },
                    colors = navItemColors
                )
                NavigationBarItem(
                    selected = tab == 4,
                    onClick = { appState.userSelectedTab = 4 },
                    icon = { Icon(Icons.Default.ConfirmationNumber, contentDescription = "My Tickets") },
                    label = { Text("My Tickets", fontSize = 10.sp) },
                    colors = navItemColors
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            when (tab) {
                0 -> MarketplaceScreen(appState)
                1 -> ListingsScreen(appState)
                2 -> CartScreen(appState)
                3 -> ChatListScreen(appState)
                4 -> OrdersScreen(appState)
                -1 -> ProfileScreen(appState)
            }
        }
    }
}
