package com.example.front_end

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.front_end.data.MockData
import com.example.front_end.model.TicketListing
import com.example.front_end.model.User
import com.example.front_end.model.UserRole
import com.example.front_end.navigation.AppState
import com.example.front_end.navigation.Screen
import com.example.front_end.service.AdminService
import com.example.front_end.service.AuthService
import com.example.front_end.service.ChatService
import com.example.front_end.service.TicketService
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardShell(appState: AppState) {
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
                2 -> ChatListContent(appState)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminUserDetail(user: User, appState: AppState, onBack: () -> Unit, onUserUpdated: () -> Unit) {
    val adminId = appState.currentUser?.userId ?: ""
    val reviews = remember(user.userId) { MockData.reviews.filter { it.targetId == user.userId } }
    val userListings = remember(user.userId) {
        TicketService.getListingsBySeller(user.userId)
    }
    var isBanned by remember { mutableStateOf(!user.isActive) }
    var showBanDialog by remember { mutableStateOf(false) }
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    if (showBanDialog) {
        AlertDialog(
            onDismissRequest = { showBanDialog = false },
            title = { Text(if (isBanned) "Unban User" else "Ban User") },
            text = {
                Text(
                    if (isBanned) "Unban ${user.name}? They will regain access."
                    else "Ban ${user.name}? They will lose access to the app."
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (isBanned) AdminService.unbanUser(user.userId)
                    else AdminService.banUser(user.userId)
                    isBanned = !isBanned
                    showBanDialog = false
                    onUserUpdated()
                }) {
                    Text(
                        if (isBanned) "Unban" else "Ban",
                        color = if (isBanned) PickTickBlue else Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showBanDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Profile", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PickTickBlue)
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF5F5F5)),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(3.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier.size(72.dp).clip(CircleShape)
                                    .background(if (isBanned) Color.LightGray else PickTickBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(44.dp), tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(user.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text(user.email, fontSize = 13.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    RoleBadge(user.role.name)
                                    if (isBanned) StatusBadge("BANNED", Color.Red)
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 14.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                            AdminStatItem(label = "Rating", value = "${"%.1f".format(user.rating)} ★")
                            AdminStatItem(label = "Reviews", value = "${user.reviewCount}")
                            AdminStatItem(label = "Joined", value = dateFormat.format(user.createdAt))
                        }
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            val thread = ChatService.getOrCreateThread(adminId, user.userId)
                            appState.navigate(Screen.ChatThread(thread.threadId))
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(containerColor = PickTickBlue),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Message", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }

                    Button(
                        onClick = { showBanDialog = true },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isBanned) PickTickBlue else Color.Red
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            if (isBanned) Icons.Default.CheckCircle else Icons.Default.Block,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (isBanned) "Unban" else "Ban User", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            item {
                Text("Reviews Received", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
            }

            if (reviews.isEmpty()) {
                item {
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                            Text("No reviews yet.", color = Color.Gray)
                        }
                    }
                }
            } else {
                items(reviews) { review ->
                    val reviewer = remember { AuthService.getUserById(review.reviewerId) }
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(reviewer?.name ?: "Unknown", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                                Text(dateFormat.format(review.createdAt), fontSize = 12.sp, color = Color.Gray)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(review.rating.toInt()) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = PickTickOrange, modifier = Modifier.size(18.dp))
                                }
                                if (review.rating % 1 != 0f) {
                                    Icon(Icons.Default.StarHalf, contentDescription = null, tint = PickTickOrange, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${"%.1f".format(review.rating)}", fontWeight = FontWeight.Bold, color = PickTickOrange)
                            }
                        }
                    }
                }
            }

            if (userListings.isNotEmpty()) {
                item {
                    Text("Listings", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
                }
                items(userListings) { listing ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(2.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(listing.title, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("${listing.date}  •  $${listing.price.toInt()}", fontSize = 13.sp, color = PickTickOrange)
                            }
                            StatusBadge(
                                listing.status.name,
                                when (listing.status.name) {
                                    "ACTIVE" -> PickTickBlue
                                    "SOLD" -> Color.Red
                                    else -> Color.Gray
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminStatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = PickTickBlue)
        Text(label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun AdminUsersTab(onUserClick: (User) -> Unit) {
    val users = remember { AdminService.getAllUsers() }

    if (users.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No users found.", color = Color.Gray)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(users) { user ->
            AdminUserCard(user = user, onClick = { onUserClick(user) })
        }
    }
}

@Composable
fun AdminUserCard(user: User, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (user.isActive) Color.White else Color(0xFFFFF0F0)
        )
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Person,
                contentDescription = null,
                tint = if (user.isActive) PickTickBlue else Color.LightGray,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(user.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(user.email, fontSize = 13.sp, color = Color.Gray)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                    RoleBadge(user.role.name)
                    if (!user.isActive) StatusBadge("BANNED", Color.Red)
                    if (user.reviewCount > 0) StatusBadge("${"%.1f".format(user.rating)} ★", PickTickOrange)
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
        }
    }
}

@Composable
fun AdminListingsTab() {
    var listings by remember { mutableStateOf(AdminService.getAllListings()) }
    var listingToDelete by remember { mutableStateOf<TicketListing?>(null) }

    if (listingToDelete != null) {
        AlertDialog(
            onDismissRequest = { listingToDelete = null },
            title = { Text("Delete Listing") },
            text = { Text("Delete \"${listingToDelete!!.title}\"? This cannot be undone.") },
            confirmButton = {
                TextButton(onClick = {
                    AdminService.deleteListing(listingToDelete!!.id)
                    listings = AdminService.getAllListings()
                    listingToDelete = null
                }) {
                    Text("Delete", color = Color.Red, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { listingToDelete = null }) { Text("Cancel") }
            }
        )
    }

    if (listings.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No listings found.", color = Color.Gray)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(listings) { listing ->
            AdminListingCard(listing = listing, onDelete = { listingToDelete = listing })
        }
    }
}

@Composable
fun AdminListingCard(listing: TicketListing, onDelete: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(14.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(listing.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(listing.eventName, fontSize = 13.sp, color = Color.Gray)
                Text(
                    "${listing.date}  •  $${listing.price.toInt()}",
                    fontSize = 13.sp,
                    color = PickTickOrange,
                    fontWeight = FontWeight.SemiBold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.padding(top = 4.dp)) {
                    StatusBadge(
                        listing.status.name,
                        when (listing.status.name) {
                            "ACTIVE" -> PickTickBlue
                            "SOLD" -> Color.Red
                            else -> Color.Gray
                        }
                    )
                    RoleBadge(listing.category)
                }
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red)
            }
        }
    }
}

@Composable
fun RoleBadge(label: String) {
    Surface(shape = RoundedCornerShape(4.dp), color = PickTickBlue.copy(alpha = 0.12f)) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = PickTickBlue,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun StatusBadge(label: String, color: Color) {
    Surface(shape = RoundedCornerShape(4.dp), color = color.copy(alpha = 0.15f)) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = color,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}
