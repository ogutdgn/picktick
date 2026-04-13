package com.example.front_end

import android.app.DatePickerDialog
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.front_end.data.MockData
import com.example.front_end.model.TicketListing
import com.example.front_end.model.UserRole
import com.example.front_end.navigation.AppState
import com.example.front_end.navigation.Screen
import com.example.front_end.service.AuthService
import com.example.front_end.service.CartService
import com.example.front_end.service.OrderService
import com.example.front_end.service.ReviewService
import com.example.front_end.service.TicketService
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerDashboardShell(appState: AppState) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("PickTick Menu", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
                HorizontalDivider()

                NavigationDrawerItem(
                    label = { Text("Marketplace") },
                    selected = true,
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    onClick = { scope.launch { drawerState.close() } }
                )
                NavigationDrawerItem(
                    label = { Text("Shopping Cart") },
                    selected = false,
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        appState.navigate(Screen.Cart)
                    }
                )
                NavigationDrawerItem(
                    label = { Text("User Profile") },
                    selected = false,
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        appState.navigate(Screen.Profile)
                    }
                )
                NavigationDrawerItem(
                    label = { Text("Chatbox") },
                    selected = false,
                    icon = { Icon(Icons.Default.Chat, contentDescription = null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        appState.navigate(Screen.ChatList)
                    }
                )

                Spacer(modifier = Modifier.weight(1f))

                NavigationDrawerItem(
                    label = { Text("Log Out") },
                    selected = false,
                    icon = { Icon(Icons.Default.ExitToApp, contentDescription = null) },
                    onClick = {
                        showLogoutDialog = true
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                Surface(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.1f), color = PickTickBlue) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp)
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                        Box(modifier = Modifier.align(Alignment.Center), contentAlignment = Alignment.Center) {
                            Text(
                                text = "🎫  PickTick  🎫",
                                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PickTickOrange, drawStyle = Stroke(miter = 10f, width = 5f))
                            )
                            Text(
                                text = "🎫  PickTick  🎫",
                                style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize().background(Color(0xFFE0E0E0))) {
                BuyerMarketplace(appState)
            }
        }

        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Log Out") },
                text = { Text("Are you sure you want to log out?") },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutDialog = false
                        appState.logout()
                    }) { Text("Yes", color = Color.Red) }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) { Text("No") }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BuyerMarketplace(appState: AppState) {
    var searchQuery by remember { mutableStateOf("") }
    val categories = listOf("All", "Music", "Sports", "Expo", "Theater", "Travel")
    var selectedCategory by remember { mutableStateOf("All") }
    val listings = remember { TicketService.getAllActiveListings() }

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

        LazyRow(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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

        PriceRangeBar()
        DateRangeFilter()

        LazyColumn(modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(16.dp)) {
            val filtered = listings.filter {
                (selectedCategory == "All" || it.category == selectedCategory) &&
                        (it.title.contains(searchQuery, ignoreCase = true) ||
                                it.location.contains(searchQuery, ignoreCase = true))
            }
            items(filtered) { listing ->
                TicketItem(listing = listing, onClick = { appState.navigate(Screen.TicketDetail(listing.id)) })
                Spacer(modifier = Modifier.height(8.dp))
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
    val backDestination = when (userRole) {
        UserRole.SELLER -> Screen.SellerDashboard
        UserRole.ADMIN -> Screen.AdminDashboard
        else -> Screen.BuyerDashboard
    }

    if (ticket == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Ticket not found.")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${ticket.title} — ${ticket.id}", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { appState.navigate(backDestination) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PickTickBlue)
            )
        },
        bottomBar = {
            if (userRole == UserRole.BUYER) {
                Surface(modifier = Modifier.fillMaxWidth(), color = Color.White, shadowElevation = 8.dp) {
                    Column(modifier = Modifier.padding(16.dp).navigationBarsPadding()) {
                        if (cartMessage.isNotEmpty()) {
                            Text(cartMessage, color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(bottom = 4.dp))
                        }
                        Button(
                            onClick = {
                                if (!addedToCart) {
                                    val success = CartService.addItem(ticket.id, userId)
                                    if (success) {
                                        addedToCart = true
                                        cartMessage = "Added to cart!"
                                    } else {
                                        cartMessage = "Already in cart or purchased."
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (addedToCart) Color.Gray else PickTickOrange
                            ),
                            enabled = !addedToCart
                        ) {
                            Text(
                                text = if (addedToCart) "Added to Cart" else "Add to Shopping Cart",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth().height(200.dp).background(Color.LightGray, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Proof of Ticket", color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(24.dp))

            DetailRow("Event", ticket.eventName)
            DetailRow("Date / Time", "${ticket.date} @ ${ticket.time}")
            DetailRow("Location", ticket.location)
            DetailRow("Price", "$${ticket.price.toInt()}")
            DetailRow("Category", ticket.category)
            DetailRow("Seat", ticket.seat)

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text("Description", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(ticket.description, color = Color.Gray)

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            val seller = remember { AuthService.getUserById(ticket.sellerId) }
            if (seller != null) {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(appState: AppState) {
    val userId = appState.currentUser?.userId ?: ""
    var cartListings by remember { mutableStateOf(CartService.getCartListings(userId)) }
    val selectedTicketIds = remember { mutableStateListOf<String>() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Cart (${cartListings.size})", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { appState.navigate(Screen.BuyerDashboard) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PickTickBlue)
            )
        },
        bottomBar = {
            if (cartListings.isNotEmpty()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Total: $${cartListings.sumOf { it.price }.let { "%.2f".format(it) }}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = PickTickBlue
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { appState.navigate(Screen.Payment) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PickTickBlue)
                    ) {
                        Text("Proceed to Payment", color = Color.White)
                    }
                }
            }
        }
    ) { padding ->
        if (cartListings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Your cart is empty!", color = Color.Gray)
            }
        } else {
            LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
                items(cartListings) { ticket ->
                    Box(modifier = Modifier.padding(vertical = 8.dp)) {
                        TicketItem(listing = ticket, onClick = {})

                        val isSelected = ticket.id in selectedTicketIds
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp)
                                .size(24.dp)
                                .background(if (isSelected) PickTickBlue else Color.White, RoundedCornerShape(4.dp))
                                .border(1.dp, PickTickBlue, RoundedCornerShape(4.dp))
                                .clickable {
                                    if (isSelected) selectedTicketIds.remove(ticket.id)
                                    else selectedTicketIds.add(ticket.id)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) Icon(Icons.Default.Check, "", tint = Color.White, modifier = Modifier.size(16.dp))
                        }

                        IconButton(
                            onClick = {
                                CartService.removeItem(ticket.id, userId)
                                cartListings = CartService.getCartListings(userId)
                                selectedTicketIds.remove(ticket.id)
                            },
                            modifier = Modifier.align(Alignment.BottomEnd)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(appState: AppState) {
    val user = appState.currentUser
    val orders = remember { if (user != null) OrderService.getOrdersByBuyer(user.userId) else emptyList() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("User Profile", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {
                        when (user?.role) {
                            UserRole.BUYER -> appState.navigate(Screen.BuyerDashboard)
                            UserRole.SELLER -> appState.navigate(Screen.SellerDashboard)
                            else -> appState.navigate(Screen.BuyerDashboard)
                        }
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PickTickBlue)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().background(Color(0xFFF8F8F8)).padding(padding).verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier.size(90.dp).clip(CircleShape).background(PickTickBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(55.dp), tint = Color.White)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(user?.name ?: "Unknown", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                    Text(user?.email ?: "", fontSize = 14.sp, color = Color.Gray)
                    Text(
                        text = "Rating: ${"%.1f".format(user?.rating ?: 0f)} (${user?.reviewCount ?: 0} reviews)",
                        fontSize = 13.sp,
                        color = PickTickOrange
                    )
                }
            }

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            Text(
                "Purchase History",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )

            if (orders.isEmpty()) {
                Text("No purchases yet.", color = Color.Gray, modifier = Modifier.padding(horizontal = 16.dp))
            } else {
                orders.forEach { order ->
                    val ticket = TicketService.getListingById(order.ticketId)
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(ticket?.title ?: order.ticketId, fontWeight = FontWeight.SemiBold)
                            Text("$${order.totalPrice.toInt()}", color = PickTickOrange, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TicketItem(listing: TicketListing, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp).clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(listing.title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = PickTickBlue, modifier = Modifier.padding(bottom = 8.dp))

            Row(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxHeight().background(Color.LightGray, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Image Placeholder", fontSize = 10.sp, color = Color.Gray)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    DetailChip(listing.date)
                    DetailChip(listing.time)
                    DetailChip(listing.location)
                    DetailChip("$${listing.price.toInt()}", isPrice = true)
                }
            }
        }
    }
}

@Composable
fun DetailChip(text: String, isPrice: Boolean = false) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (isPrice) PickTickOrange.copy(alpha = 0.2f) else Color(0xFFF0F0F0),
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            fontSize = 12.sp,
            fontWeight = if (isPrice) FontWeight.Bold else FontWeight.Normal,
            color = if (isPrice) PickTickOrange else Color.DarkGray
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PriceRangeBar() {
    var minPriceInput by remember { mutableStateOf("0") }
    var maxPriceInput by remember { mutableStateOf("9999") }

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)) {
        Text(text = "Price Range", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = minPriceInput,
                onValueChange = { if (it.all { c -> c.isDigit() }) minPriceInput = it },
                modifier = Modifier.weight(1f),
                label = { Text("Min $") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            Text("-", fontWeight = FontWeight.Bold)
            OutlinedTextField(
                value = maxPriceInput,
                onValueChange = { if (it.all { c -> c.isDigit() }) maxPriceInput = it },
                modifier = Modifier.weight(1f),
                label = { Text("Max $") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
        }
    }
}

@Composable
fun DateRangeFilter() {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()
    var startDate by remember { mutableStateOf("Start Date") }
    var endDate by remember { mutableStateOf("End Date") }

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
        Text(text = "Date", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = { showDatePicker { startDate = it } },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(text = startDate, color = Color.DarkGray, fontSize = 12.sp)
            }
            OutlinedButton(
                onClick = { showDatePicker { endDate = it } },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color.LightGray)
            ) {
                Icon(Icons.Default.DateRange, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text(text = endDate, color = Color.DarkGray, fontSize = 12.sp)
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
        TicketService.getListingsBySeller(userId).filter { it.status.name == "ACTIVE" }
    }
    val backDestination = when (appState.currentUser?.role) {
        UserRole.SELLER -> Screen.SellerDashboard
        UserRole.ADMIN -> Screen.AdminDashboard
        else -> Screen.BuyerDashboard
    }

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
                    IconButton(onClick = { appState.navigate(backDestination) }) {
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
