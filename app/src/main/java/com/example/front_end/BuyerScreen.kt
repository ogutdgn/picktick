package com.example.front_end

import android.app.DatePickerDialog
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.front_end.data.MockData
import com.example.front_end.model.ListingStatus
import com.example.front_end.model.TicketListing
import com.example.front_end.model.UserRole
import com.example.front_end.navigation.AppState
import com.example.front_end.navigation.Screen
import com.example.front_end.service.AuthService
import com.example.front_end.service.CartService
import com.example.front_end.service.ChatService
import com.example.front_end.service.OrderService
import com.example.front_end.service.ReviewService
import com.example.front_end.service.TicketService
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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
fun UserDashboardShell(appState: AppState) {
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
                0 -> UserMarketplace(appState)
                1 -> MyListingsContent(appState)
                2 -> CartContent(appState)
                3 -> ChatListContent(appState)
                4 -> MyTicketsContent(appState)
                -1 -> ProfileContent(appState)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserMarketplace(appState: AppState) {
    val userId = appState.currentUser?.userId ?: ""
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf("All", "Music", "Sports", "Expo", "Theater", "Travel")
    var selectedCategory by remember { mutableStateOf("All") }
    var filtersExpanded by remember { mutableStateOf(false) }

    var draftMinPrice by remember { mutableStateOf(appState.filterMinPrice) }
    var draftMaxPrice by remember { mutableStateOf(appState.filterMaxPrice) }
    var draftStartDate by remember { mutableStateOf(appState.filterStartDate) }
    var draftEndDate by remember { mutableStateOf(appState.filterEndDate) }

    val listings = remember { TicketService.getAllActiveListings().filter { it.sellerId != userId } }

    val minP = appState.filterMinPrice.toDoubleOrNull() ?: 0.0
    val maxP = appState.filterMaxPrice.toDoubleOrNull() ?: 9999.0

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            placeholder = { Text("Search", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray) },
            shape = RoundedCornerShape(25.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedBorderColor = PickTickOrange,
                unfocusedBorderColor = Color.Transparent
            ),
            singleLine = true
        )

        Row(
            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            LazyRow(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(categories) { category ->
                    FilterChip(
                        selected = (category == selectedCategory),
                        onClick = { selectedCategory = category },
                        label = { Text(category) }
                    )
                }
            }
            TextButton(onClick = { filtersExpanded = !filtersExpanded }) {
                Text(
                    if (filtersExpanded) "▲" else "▼",
                    color = PickTickBlue,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }

        AnimatedVisibility(visible = filtersExpanded) {
            Column {
                PriceRangeBar(
                    minPrice = draftMinPrice,
                    maxPrice = draftMaxPrice,
                    onMinChange = { draftMinPrice = it },
                    onMaxChange = { draftMaxPrice = it }
                )
                DateRangeFilter(
                    startDate = draftStartDate,
                    endDate = draftEndDate,
                    onStartChange = { draftStartDate = it },
                    onEndChange = { draftEndDate = it }
                )
                Button(
                    onClick = {
                        appState.filterMinPrice = draftMinPrice
                        appState.filterMaxPrice = draftMaxPrice
                        appState.filterStartDate = draftStartDate
                        appState.filterEndDate = draftEndDate
                        filtersExpanded = false
                    },
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp).height(44.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PickTickBlue)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Save & Apply Filters", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        val filtered = listings.filter {
            (selectedCategory == "All" || it.category == selectedCategory) &&
            (it.title.contains(searchQuery, ignoreCase = true) || it.location.contains(searchQuery, ignoreCase = true)) &&
            it.price >= minP && it.price <= maxP
        }

        if (filtered.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.SearchOff, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Text("Can't find any listings.", color = Color.Gray, fontSize = 15.sp)
                }
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
                items(filtered) { listing ->
                    TicketItem(listing = listing, onClick = { appState.navigate(Screen.TicketDetail(listing.id)) })
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun CartContent(appState: AppState) {
    val userId = appState.currentUser?.userId ?: ""
    var cartListings by remember { mutableStateOf(CartService.getCartListings(userId)) }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        Text(
            "My Cart (${cartListings.size})",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )

        if (cartListings.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Your cart is empty!", color = Color.Gray, fontSize = 16.sp)
            }
        } else {
            LazyColumn(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
                items(cartListings) { ticket ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        elevation = CardDefaults.cardElevation(3.dp),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp).fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(ticket.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = PickTickBlue)
                                Text(ticket.eventName, fontSize = 12.sp, color = Color.Gray)
                                Text("${ticket.date}  •  ${ticket.time}", fontSize = 12.sp, color = Color.Gray)
                                Text(ticket.location, fontSize = 12.sp, color = Color.Gray)
                                Text(
                                    "$${ticket.price.toInt()}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = PickTickOrange
                                )
                            }
                            IconButton(
                                onClick = {
                                    CartService.removeItem(ticket.id, userId)
                                    cartListings = CartService.getCartListings(userId)
                                }
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
                            }
                        }
                    }
                }
            }

            Surface(modifier = Modifier.fillMaxWidth(), color = Color.White, shadowElevation = 6.dp) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total", fontSize = 16.sp, color = Color.Gray)
                        Text(
                            "$${"%.2f".format(cartListings.sumOf { it.price })}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = PickTickBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = { appState.navigate(Screen.Payment) },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PickTickBlue)
                    ) {
                        Text("Proceed to Payment", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MyTicketsContent(appState: AppState) {
    val userId = appState.currentUser?.userId ?: ""
    val orders = remember { OrderService.getOrdersByBuyer(userId) }
    var activeTab by remember { mutableStateOf(0) }
    val now = Date()
    val dateFmt = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    val todayStart = remember {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.time
    }

    val (activeOrders, pastOrders) = remember(orders) {
        orders.partition { order ->
            val ticket = TicketService.getListingById(order.ticketId)
            if (ticket != null) {
                try {
                    val eventDate = dateFmt.parse(ticket.date)
                    eventDate != null && !eventDate.before(todayStart)
                } catch (e: Exception) { true }
            } else true
        }
    }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        TabRow(
            selectedTabIndex = activeTab,
            containerColor = Color.White,
            contentColor = PickTickBlue
        ) {
            Tab(
                selected = activeTab == 0,
                onClick = { activeTab = 0 },
                text = { Text("Active", fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal) }
            )
            Tab(
                selected = activeTab == 1,
                onClick = { activeTab = 1 },
                text = { Text("Past", fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal) }
            )
        }

        val displayOrders = if (activeTab == 0) activeOrders else pastOrders

        if (displayOrders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.ConfirmationNumber, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color.LightGray)
                    Text(
                        if (activeTab == 0) "No active tickets." else "No past tickets.",
                        color = Color.Gray, fontSize = 15.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(displayOrders) { order ->
                    val ticket = TicketService.getListingById(order.ticketId)
                    if (ticket != null) {
                        Card(
                            shape = RoundedCornerShape(14.dp),
                            elevation = CardDefaults.cardElevation(3.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(ticket.title, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PickTickBlue)
                                Text(ticket.eventName, fontSize = 13.sp, color = Color.Gray)
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Text("📅 ${ticket.date}", fontSize = 13.sp)
                                    Text("🕐 ${ticket.time}", fontSize = 13.sp)
                                }
                                Text("📍 ${ticket.location}", fontSize = 13.sp)
                                Text("💺 ${ticket.seat}", fontSize = 13.sp)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text("Confirmation Code", fontSize = 11.sp, color = Color.Gray)
                                        Text(
                                            ticket.proofCode,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp,
                                            color = PickTickOrange
                                        )
                                    }
                                    Text(
                                        "$${"%.2f".format(order.totalPrice)}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = PickTickBlue
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileContent(appState: AppState) {
    val user = remember { AuthService.getUserById(appState.currentUser?.userId ?: "") }
    val liveRating = remember { if (user != null) ReviewService.getAverageRating(user.userId) else 0f }
    val liveReviewCount = remember { if (user != null) ReviewService.getReviewCount(user.userId) else 0 }

    Column(modifier = Modifier.fillMaxSize().background(Color(0xFFF8F8F8))) {
        TextButton(
            onClick = { appState.userSelectedTab = 0 },
            modifier = Modifier.padding(start = 8.dp, top = 4.dp)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(18.dp), tint = PickTickBlue)
            Spacer(modifier = Modifier.width(4.dp))
            Text("Go Back", color = PickTickBlue, fontWeight = FontWeight.SemiBold)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(14.dp),
                    elevation = CardDefaults.cardElevation(3.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp).fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(72.dp).clip(CircleShape).background(PickTickBlue),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(44.dp), tint = Color.White)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(user?.name ?: "Unknown", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                            Text(user?.email ?: "", fontSize = 13.sp, color = Color.Gray)
                            Text(
                                user?.role?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "",
                                fontSize = 12.sp,
                                color = PickTickBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                            if (liveReviewCount > 0) {
                                Text(
                                    "${"%.1f".format(liveRating)} ★  ($liveReviewCount reviews)",
                                    fontSize = 13.sp,
                                    color = PickTickOrange
                                )
                            }
                        }
                    }
                }
            }

            when (user?.role) {
                UserRole.USER -> {
                    item {
                        Text("Purchase History", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
                    }
                    val purchases = OrderService.getOrdersByBuyer(user.userId)
                    if (purchases.isEmpty()) {
                        item { Text("No purchases yet.", color = Color.Gray) }
                    } else {
                        items(purchases) { order ->
                            val ticket = TicketService.getListingById(order.ticketId)
                            val seller = AuthService.getUserById(order.sellerId)
                            var showReviewDialog by remember { mutableStateOf(false) }
                            val alreadyReviewed = remember { ReviewService.hasUserReviewed(user.userId, order.sellerId) }

                            if (showReviewDialog) {
                                ReviewDialog(
                                    targetName = seller?.name ?: "Seller",
                                    onDismiss = { showReviewDialog = false },
                                    onSubmit = { rating ->
                                        ReviewService.submitReview(user.userId, order.sellerId, rating)
                                        showReviewDialog = false
                                    }
                                )
                            }

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(2.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(ticket?.title ?: order.ticketId, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Text("Seller: ${seller?.name ?: "Unknown"}", fontSize = 12.sp, color = Color.Gray)
                                        }
                                        Text("$${order.totalPrice.toInt()}", color = PickTickOrange, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                    if (!alreadyReviewed) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedButton(
                                            onClick = { showReviewDialog = true },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp), tint = PickTickOrange)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Rate Seller", color = PickTickOrange)
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("✓ Reviewed", fontSize = 12.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Sales History", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
                    }
                    val sales = OrderService.getOrdersBySeller(user.userId)
                    if (sales.isEmpty()) {
                        item { Text("No sales yet.", color = Color.Gray) }
                    } else {
                        items(sales) { order ->
                            val ticket = TicketService.getListingById(order.ticketId)
                            val buyer = AuthService.getUserById(order.buyerId)
                            var showReviewDialog by remember { mutableStateOf(false) }
                            val alreadyReviewed = remember { ReviewService.hasUserReviewed(user.userId, order.buyerId) }

                            if (showReviewDialog) {
                                ReviewDialog(
                                    targetName = buyer?.name ?: "Buyer",
                                    onDismiss = { showReviewDialog = false },
                                    onSubmit = { rating ->
                                        ReviewService.submitReview(user.userId, order.buyerId, rating)
                                        showReviewDialog = false
                                    }
                                )
                            }

                            Card(
                                shape = RoundedCornerShape(12.dp),
                                elevation = CardDefaults.cardElevation(2.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(ticket?.title ?: order.ticketId, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                            Text("Buyer: ${buyer?.name ?: "Unknown"}", fontSize = 12.sp, color = Color.Gray)
                                        }
                                        Text("$${order.totalPrice.toInt()}", color = Color(0xFF4CAF50), fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                    }
                                    if (!alreadyReviewed) {
                                        Spacer(modifier = Modifier.height(8.dp))
                                        OutlinedButton(
                                            onClick = { showReviewDialog = true },
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(16.dp), tint = PickTickOrange)
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text("Rate Buyer", color = PickTickOrange)
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text("✓ Reviewed", fontSize = 12.sp, color = Color.Gray)
                                    }
                                }
                            }
                        }
                    }
                }

                UserRole.ADMIN -> {
                    item {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                Text("Admin accounts do not have purchase or sales history.", color = Color.Gray, textAlign = TextAlign.Center)
                            }
                        }
                    }
                }

                else -> {}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(appState: AppState, ticketId: String) {
    val ticket = remember { TicketService.getListingById(ticketId) }
    val userId = appState.currentUser?.userId ?: ""
    val userRole = appState.currentUser?.role
    val isInCart = remember(ticketId) { CartService.isInCart(ticketId, userId) }
    var addedToCart by remember { mutableStateOf(isInCart) }
    var cartMessage by remember { mutableStateOf("") }

    if (ticket == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Ticket not found.")
        }
        return
    }

    val isOwnListing = ticket.sellerId == userId

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(ticket.title, color = Color.White, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (userRole == UserRole.ADMIN) {
                            appState.navigate(Screen.AdminDashboard)
                        } else {
                            appState.userSelectedTab = 0
                            appState.navigate(Screen.UserDashboard)
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PickTickBlue)
            )
        },
        bottomBar = {
            if (userRole == UserRole.USER && !isOwnListing) {
                Surface(modifier = Modifier.fillMaxWidth(), color = Color.White, shadowElevation = 8.dp) {
                    Column(modifier = Modifier.padding(16.dp).navigationBarsPadding()) {
                        if (cartMessage.isNotEmpty()) {
                            Text(cartMessage, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
                        }
                        Button(
                            onClick = {
                                if (!addedToCart) {
                                    val success = CartService.addItem(ticket.id, userId)
                                    if (success) {
                                        addedToCart = true
                                        cartMessage = ""
                                    } else {
                                        cartMessage = "Already in cart or already purchased."
                                    }
                                } else {
                                    appState.navigate(Screen.Payment)
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (addedToCart) Color(0xFF4CAF50) else PickTickOrange
                            )
                        ) {
                            if (addedToCart) {
                                Icon(Icons.Default.ShoppingCartCheckout, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Go to Checkout", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            } else {
                                Icon(Icons.Default.ShoppingCart, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Add to Shopping Cart", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            DetailRow("Event", ticket.eventName)
            DetailRow("Date / Time", "${ticket.date} @ ${ticket.time}")
            DetailRow("Location", ticket.location)
            DetailRow("Price", "$${ticket.price.toInt()}")
            DetailRow("Category", ticket.category)
            DetailRow("Seat", ticket.seat)

            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = PickTickOrange.copy(alpha = 0.08f)),
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = PickTickOrange, modifier = Modifier.size(20.dp))
                    Column {
                        Text("Confirmation Code", fontSize = 12.sp, color = PickTickOrange, fontWeight = FontWeight.SemiBold)
                        Text("Will be provided to the buyer after purchase.", fontSize = 12.sp, color = Color.Gray)
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text("Description", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(ticket.description, color = Color.Gray)

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            val seller = remember { AuthService.getUserById(ticket.sellerId) }
            if (seller != null && seller.userId != userId) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { appState.navigate(Screen.PublicProfile(seller.userId)) }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Seller", fontSize = 12.sp, color = Color.Gray)
                        Text(seller.name, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = PickTickBlue)
                        Text(
                            "${"%.1f".format(seller.rating)} ★  (${seller.reviewCount} reviews)",
                            fontSize = 13.sp,
                            color = PickTickOrange
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color.LightGray)
                }
            }
        }
    }
}

@Composable
fun ReviewDialog(targetName: String, onDismiss: () -> Unit, onSubmit: (Float) -> Unit) {
    var selectedRating by remember { mutableStateOf(0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rate $targetName") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("How was your experience?", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    for (i in 1..5) {
                        Icon(
                            imageVector = if (i <= selectedRating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = PickTickOrange,
                            modifier = Modifier.size(36.dp).clickable { selectedRating = i }
                        )
                    }
                }
                if (selectedRating > 0) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = when (selectedRating) {
                            1 -> "Poor"
                            2 -> "Fair"
                            3 -> "Good"
                            4 -> "Very Good"
                            else -> "Excellent!"
                        },
                        color = PickTickOrange,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { if (selectedRating > 0) onSubmit(selectedRating.toFloat()) },
                enabled = selectedRating > 0
            ) {
                Text("Submit", color = if (selectedRating > 0) PickTickBlue else Color.Gray, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel", color = Color.Gray) }
        }
    )
}

@Composable
fun TicketItem(listing: TicketListing, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text(listing.title, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = PickTickBlue)
            Text(listing.eventName, fontSize = 13.sp, color = Color.Gray)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                DetailChip(listing.date)
                DetailChip(listing.time)
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                DetailChip(listing.location, modifier = Modifier.weight(1f))
                Spacer(Modifier.width(8.dp))
                DetailChip("$${listing.price.toInt()}", isPrice = true)
            }
        }
    }
}

@Composable
fun DetailChip(text: String, isPrice: Boolean = false, modifier: Modifier = Modifier) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isPrice) PickTickOrange.copy(alpha = 0.2f) else Color(0xFFF0F0F0),
        modifier = modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = if (isPrice) FontWeight.Bold else FontWeight.Normal,
            color = if (isPrice) PickTickOrange else Color.DarkGray,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text("$label: ", fontWeight = FontWeight.Bold, color = PickTickBlue)
        Text(value)
    }
}

@Composable
fun PriceRangeBar(minPrice: String, maxPrice: String, onMinChange: (String) -> Unit, onMaxChange: (String) -> Unit) {
    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
        Text(text = "Price Range", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = minPrice,
                onValueChange = { if (it.all { c -> c.isDigit() }) onMinChange(it) },
                modifier = Modifier.weight(1f),
                label = { Text("Min $") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            Text("-", fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = maxPrice,
                onValueChange = { if (it.all { c -> c.isDigit() }) onMaxChange(it) },
                modifier = Modifier.weight(1f),
                label = { Text("Max $") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

@Composable
fun DateRangeFilter(startDate: String, endDate: String, onStartChange: (String) -> Unit, onEndChange: (String) -> Unit) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    fun showDatePicker(onDateSelected: (String) -> Unit) {
        DatePickerDialog(
            context,
            { _, year, month, day -> onDateSelected("$day/${month + 1}/$year") },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
        Text(text = "Date Range", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { showDatePicker { onStartChange(it) } },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(text = startDate.ifEmpty { "Start Date" }, color = Color.DarkGray, fontSize = 12.sp)
            }
            OutlinedButton(
                onClick = { showDatePicker { onEndChange(it) } },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(6.dp))
                Text(text = endDate.ifEmpty { "End Date" }, color = Color.DarkGray, fontSize = 12.sp)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PublicProfileScreen(appState: AppState, userId: String) {
    val user = remember { AuthService.getUserById(userId) }
    val reviews = remember(userId) { MockData.reviews.filter { it.targetId == userId } }
    val listings = remember(userId) {
        TicketService.getListingsBySeller(userId).filter { it.status == ListingStatus.ACTIVE }
    }
    val currentUserId = appState.currentUser?.userId ?: ""

    if (user == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("User not found.")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(user.name, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {
                        if (appState.currentUser?.role == UserRole.ADMIN) {
                            appState.navigate(Screen.AdminDashboard)
                        } else {
                            appState.navigate(Screen.UserDashboard)
                        }
                    }) {
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
                                modifier = Modifier.size(72.dp).clip(CircleShape).background(PickTickBlue),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(44.dp), tint = Color.White)
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(user.name, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                                Text(user.role.name.lowercase().replaceFirstChar { it.uppercase() }, fontSize = 13.sp, color = Color.Gray)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "${"%.1f".format(user.rating)} ★  (${user.reviewCount} reviews)",
                                    fontSize = 14.sp,
                                    color = PickTickOrange,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }

            if (currentUserId != userId) {
                item {
                    Button(
                        onClick = {
                            val thread = ChatService.getOrCreateThread(currentUserId, userId)
                            appState.navigate(Screen.ChatThread(thread.threadId))
                        },
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PickTickBlue)
                    ) {
                        Icon(Icons.Default.Chat, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Message", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }

            if (listings.isNotEmpty()) {
                item {
                    Text("Active Listings", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
                }
                items(listings) { listing ->
                    TicketItem(listing = listing, onClick = { appState.navigate(Screen.TicketDetail(listing.id)) })
                }
            }

            item {
                Text("Reviews", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
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
                            Text(reviewer?.name ?: "Unknown", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                repeat(review.rating.toInt()) {
                                    Icon(Icons.Default.Star, contentDescription = null, tint = PickTickOrange, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("${"%.1f".format(review.rating)}", fontWeight = FontWeight.Bold, color = PickTickOrange)
                            }
                        }
                    }
                }
            }
        }
    }
}
