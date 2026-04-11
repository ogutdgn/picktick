package com.example.front_end

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import kotlinx.coroutines.delay
import androidx.compose.ui.draw.rotate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Dashboard



val PickTickBlue = Color(0xFF0074BD)
val PickTickOrange = Color(0xFFFF9800)


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainNavigation()
        }
    }
    // hello
}

enum class UserType { BUYER, SELLER, ADMIN, NONE }

@Composable
fun MainNavigation() {
    // 0 = Splash, 1 = Login, 2 = Register, 3 = Dashboard, 4 = Role Selection
    var screenState by remember { mutableIntStateOf(0) }
    var currentUserType by remember { mutableStateOf(UserType.NONE) }
    var selectedTicket by remember { mutableStateOf<TicketListing?>(null) }
    val myCartList = remember { mutableStateListOf<TicketListing>() }
    var currentScreen by remember { mutableStateOf("dashboard") }
    // This is the timer that moves from Splash to Login
    LaunchedEffect(Unit) {
        delay(1000) // Wait 1 second
        screenState = 1 // Switch to LoginScreen
    }

    Crossfade(
        targetState = screenState,
        animationSpec = tween(1000), // Smooth 1-second fade
        label = "ScreenTransition"
    ) { state ->
        when (state) {
            0 -> PickTickSplashScreen()

            1 -> LoginScreen(
                onLoginSuccess = {
                    // When login is pressed, go to Role Selection (State 4)
                    screenState = 4
                },
                onNavigateToRegister = { screenState = 2 },
                onNavigateToReset = { screenState = 6 }
            )

            2 -> RegisterScreen(
                onNavigateToLogin = { screenState = 1 }
            )
            6 -> PassResetScreen(onBackToLogin = { screenState = 1 })

            4 -> RoleSelectionScreen(
                onRoleSelected = { type ->
                    currentUserType = type
                    screenState = 3 // Go to Dashboard
                }
            )

            3 -> PickTickDashboard(
                userType = currentUserType,
                cartList = myCartList,
                onTicketClick = { ticket ->
                    selectedTicket = ticket
                    screenState = 5
                },
                onLogout = { screenState = 1 }

            )
            5 -> TicketDetailScreen(
                ticket = selectedTicket,
                onBack = { screenState = 3 },
                userType = currentUserType, // This tells the screen who is looking
                onAddToCart = { ticketToAdd ->
                    if (!myCartList.contains(ticketToAdd)) {
                        myCartList.add(ticketToAdd)
                    }
                }
            )
        }
    }
}

@Composable
fun PickTickSplashScreen() {
    Box(
        modifier = Modifier.fillMaxSize().background(PickTickBlue),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {

            // Left Emoji
            Text(text = "🎫", fontSize = 40.sp, modifier = Modifier.rotate(-15f))

            Spacer(modifier = Modifier.width(16.dp))

            // The Text Logo
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

            // Right Emoji
            Text(text = "🎫", fontSize = 40.sp, modifier = Modifier.rotate(15f))
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: () -> Unit, onNavigateToRegister: () -> Unit, onNavigateToReset: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome to",
            fontSize = 20.sp,
            color = Color.Gray,
            fontWeight = FontWeight.Medium
        )

        // The Stylized PickTick Logo Row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 32.dp)
        ) {
            // "Pick" Section: Blue Fill, Orange Border
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "Pick",
                    style = TextStyle(
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        drawStyle = Stroke(miter = 10f, width = 5f),
                        color = PickTickOrange // The Border
                    )
                )
                Text(
                    text = "Pick",
                    style = TextStyle(
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PickTickBlue // The Fill
                    )
                )
            }

            // "Tick" Section: Orange Fill, Blue Border
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "Tick",
                    style = TextStyle(
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        drawStyle = Stroke(miter = 10f, width = 5f),
                        color = PickTickBlue // The Border
                    )
                )
                Text(
                    text = "Tick",
                    style = TextStyle(
                        fontSize = 48.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = PickTickOrange // The Fill
                    )
                )
            }
        }

        OutlinedTextField(
            value = "", onValueChange = {}, label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        )
        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = "", onValueChange = {}, label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        )

        // SIGN UP LINK LEFT AND FORGETPASS RIGHT
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween // Pushes items to opposite sides
        ) {
            TextButton(onClick = onNavigateToRegister) {
                Text("Sign up", color = PickTickBlue, fontWeight = FontWeight.Bold)
            }

            TextButton(onClick = onNavigateToReset) {
                Text("Forgot your password?", color = PickTickOrange, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLoginSuccess, // Temporary: No password check needed
            colors = ButtonDefaults.buttonColors(containerColor = PickTickBlue),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
        ) {
            Text("Login", color = Color.White)
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(onNavigateToLogin: () -> Unit) {
    // 1. State for all your fields
    var fullName by remember { mutableStateOf("") }
    var dob by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var userType by remember { mutableStateOf("Buyer") } // Default value

    // State for the DatePicker and Dropdown menus
    var showDatePicker by remember { mutableStateOf(false) }
    var showTypeMenu by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState()), // Allows scrolling on smaller screens
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Create Account", fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, color = PickTickBlue)
        Spacer(modifier = Modifier.height(24.dp))

        // Full Name
        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text("Full Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        // 1. Date of Birth (Click to open Calendar)
        OutlinedTextField(
            value = dob,
            onValueChange = { },
            label = { Text("Date of Birth") },
            readOnly = true, // Prevent manual typing
            trailingIcon = {
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        // 2. Username
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(12.dp))

        // 3. User Type Dropdown (Buyer or Seller)
        ExposedDropdownMenuBox(
            expanded = showTypeMenu,
            onExpandedChange = { showTypeMenu = !showTypeMenu },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = userType,
                onValueChange = {},
                readOnly = true,
                label = { Text("I am a...") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showTypeMenu) },
                modifier = Modifier.menuAnchor().fillMaxWidth()
            )
            ExposedDropdownMenu(
                expanded = showTypeMenu,
                onDismissRequest = { showTypeMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Buyer") },
                    onClick = { userType = "Buyer"; showTypeMenu = false }
                )
                DropdownMenuItem(
                    text = { Text("Seller") },
                    onClick = { userType = "Seller"; showTypeMenu = false }
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Email & Password
        OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("New Password") }, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { /* Save these states later */ },
            colors = ButtonDefaults.buttonColors(containerColor = PickTickOrange),
            modifier = Modifier.fillMaxWidth().height(50.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Sign Up", color = Color.White)
        }

        TextButton(onClick = onNavigateToLogin) {
            Text("Already have an account? Login", color = Color.Gray)
        }
    }

    // --- Date Picker Dialog Logic ---
    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selectedDate = datePickerState.selectedDateMillis
                    if (selectedDate != null) {
                        // Simple formatting (You can use a proper formatter here)
                        dob = java.text.SimpleDateFormat("MM/dd/yyyy", java.util.Locale.getDefault()).format(java.util.Date(selectedDate))
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
fun PassResetScreen(onBackToLogin: () -> Unit) {
    // 1 = Enter Email, 2 = Enter Confirmation, 3 = New Password
    var step by remember { mutableStateOf(1) }
    var email by remember { mutableStateOf("") }
    var code by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Reset Password",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = PickTickBlue
        )
        Spacer(modifier = Modifier.height(24.dp))

        when (step) {
            1 -> {
                // STEP 1: ENTER EMAIL
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Enter your email") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { step = 2 },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PickTickBlue)
                ) {
                    Text("Send Code")
                }
            }
            2 -> {
                // STEP 2: ENTER CONFIRMATION
                OutlinedTextField(
                    value = code,
                    onValueChange = { code = it },
                    label = { Text("Enter Confirmation Code") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { step = 3 },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PickTickBlue)
                ) {
                    Text("Verify Code")
                }
            }
            3 -> {
                // STEP 3: NEW PASSWORD
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New Password") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = onBackToLogin, // Lead back to Login
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PickTickOrange)
                ) {
                    Text("Confirm New Password")
                }
            }
        }

        TextButton(onClick = onBackToLogin) {
            Text("Cancel", color = Color.Gray)
        }
    }
}

@Composable
fun RoleSelectionScreen(onRoleSelected: (UserType) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().background(PickTickBlue).padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Select Your Role", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = PickTickBlue)
        Spacer(modifier = Modifier.height(32.dp))

        val roles = listOf(
            UserType.BUYER to Color.Cyan,
            UserType.SELLER to Color.Yellow, // Green
            UserType.ADMIN to Color.Red
        )

        roles.forEach { (role, color) ->
            Button(
                onClick = { onRoleSelected(role) },
                colors = ButtonDefaults.buttonColors(containerColor = color),
                modifier = Modifier.fillMaxWidth().height(55.dp).padding(vertical = 4.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            ) {
                Text(role.name, color = PickTickBlue, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PickTickDashboard(
    userType: UserType,
    onTicketClick: (TicketListing) -> Unit, // This triggers the ticket detail screen
    cartList: MutableList<TicketListing>,
    onLogout: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedTicket by remember { mutableStateOf<TicketListing?>(null) }
    var currentScreen by remember { mutableStateOf("dashboard") }
    var showLogoutDialog by remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Text("PickTick Menu", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold)
                HorizontalDivider()

                // Common Items
                NavigationDrawerItem(
                    label = { Text("Marketplace") },
                    selected = currentScreen == "dashboard",
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    onClick = {
                        currentScreen = "dashboard"
                        scope.launch { drawerState.close() }
                    }
                )

                NavigationDrawerItem(
                    label = { Text("User Profile") },
                    selected = currentScreen == "profile",
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    onClick = {
                        currentScreen = "profile"
                        scope.launch { drawerState.close() }
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Chatbox") },
                    selected = currentScreen == "chatbox",
                    icon = { Icon(Icons.Default.Chat, contentDescription = null) },
                    onClick = {
                        currentScreen = "chatbox"
                        scope.launch { drawerState.close() }
                    }
                )

                // Role Specific Items
                if (userType == UserType.BUYER) {
                    NavigationDrawerItem(
                        label = { Text("Shopping Cart") },
                        selected = currentScreen == "shopping_cart",
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                        onClick = {
                            currentScreen = "shopping_cart"
                            scope.launch { drawerState.close() }
                        }
                    )
                }

                if (userType == UserType.SELLER) {
                    NavigationDrawerItem(
                        label = { Text("Sales Dashboard") },
                        icon = { Icon(Icons.Default.Dashboard, contentDescription = null) },
                        selected = false,
                        onClick = { currentScreen = "sales_dashboard"
                            scope.launch { drawerState.close() } }
                    )
                }

                if (userType == UserType.ADMIN) {
                    NavigationDrawerItem(
                        label = { Text("System Logs") },
                        selected = false,
                        onClick = { /* Implement Admin Logs Later */ }
                    )
                }

                Spacer(modifier = Modifier.weight(1f)) // Push logout to the bottom

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
                Surface(
                    modifier = Modifier.fillMaxWidth().fillMaxHeight(0.1f),
                    color = PickTickBlue
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        IconButton(
                            onClick = { scope.launch { drawerState.open() } },
                            modifier = Modifier.align(Alignment.CenterStart).padding(start = 8.dp)
                        ) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }

                        Box(modifier = Modifier.align(Alignment.Center), contentAlignment = Alignment.Center) {
                            val logoText = "🎫  PickTick  🎫"
                            Text(
                                text = logoText,
                                style = TextStyle(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PickTickOrange,
                                    drawStyle = Stroke(miter = 10f, width = 5f)
                                )
                            )
                            Text(
                                text = logoText,
                                style = TextStyle(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
                    .background(Color(0xFFE0E0E0))
            ) {
                when (currentScreen) {
                    "shopping_cart" -> {
                        ShoppingCartScreen(
                            cartTickets = cartList,
                            onBack = { currentScreen = "dashboard" },
                            onCheckout = { currentScreen = "payment" }
                        )
                    }
                    "profile" -> {
                        // Pass the userType variable here
                        UserProfileScreen(
                            userType = userType,
                            onBack = { currentScreen = "dashboard" }
                        )
                    }
                    "chatbox" -> {
                        ChatboxScreen()
                    }
                    "sales_dashboard" -> {
                        SalesDashboardScreen(
                            onListNewTicket = { currentScreen = "create_ticket" },
                            onModifyTicket = { ticket ->
                                selectedTicket = ticket
                                currentScreen = "modify_ticket"
                            }
                        )
                    }
                    "create_ticket" -> {
                        TicketCreateScreen(
                            onBack = { currentScreen = "sales_dashboard" }
                        )
                    }
                    "modify_ticket" -> {
                        selectedTicket?.let {
                            ModifyListingScreen(ticket = it, onBack = { currentScreen = "sales_dashboard" })
                        }
                    }


                    // Handle payment screen if you have it
                    "payment" -> {
                        // Your PaymentScreen call here
                    }
                    else -> {
                        // The "Marketplace" section
                        when (userType) {
                            UserType.BUYER -> {
                                BuyerDashboard(
                                    onTicketClick = onTicketClick,
                                    cartList = cartList
                                )
                            }
                            UserType.SELLER -> {
                                // FIX: Added the onTicketClick parameter
                                SellerDashboard(onTicketClick = onTicketClick)
                            }
                            UserType.ADMIN -> {
                                // FIX: Ensure your AdminDashboard is defined or use a placeholder
                                Text("Admin Marketplace View", modifier = Modifier.align(Alignment.Center))
                            }
                            else -> Text("Please select a valid role.")
                        }
                    }
                }
            }
        }

        // Logout Confirmation Dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                title = { Text("Log Out") },
                text = { Text("Are you sure you want to Log out your account?") },
                confirmButton = {
                    TextButton(onClick = {
                        showLogoutDialog = false
                        onLogout()
                    }) {
                        Text("Yes", color = Color.Red)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("No")
                    }
                }
            )
        }
    }
}
data class TicketListing(
    val id: String, // Unique identifier (e.g., "PT-9921")
    val title: String,
    val eventName: String,
    val date: String,
    val time: String,
    val location: String,
    val price: String,
    val category: String,
    val seat: String,
    val description: String
)

val fakeListings = listOf(
    TicketListing("0001","Music Festival 2026", "Music Festival of New York 2026","Oct 12", "7:00 PM", "New Tork, NY", "$120", "Music","B2","Nice view on the front, easily capture all events"),
    TicketListing("0002","Tech Conference", "Tech Expo of Texas","Nov 05", "9:00 AM", "Arlington, TX", "$50","Expo", "N/A", ""),
    TicketListing("0003","Train to Chicago","Traintrip from Texas to Chicago", "Dec 20", "6:30 PM", "Fort Worth, TX", "$250","Travel","F15","Nice seat by the window, easy to view the landscape!!"),
    TicketListing("0004", "Music Festival 2026", "Music Festival of Texas 2026", "Oct 12", "7:00 PM", "Dallas, TX", "$120", "Music", "A1", "Standard VIP entry."),
    TicketListing("0005", "Tech Conference", "Tech Expo of Texas", "Nov 05", "9:00 AM", "Arlington, TX", "$50", "Expo", "N/A", "Full access to all booths.")
)

@Composable
fun TicketItem(listing: TicketListing , onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() }, //Trigger Navigation
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // PART 1: Title on Top
            Text(
                text = listing.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = PickTickBlue,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Row(modifier = Modifier.fillMaxWidth().height(120.dp)) {
                // PART 2: Image Area (1/2 Left)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(Color.LightGray, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Image Placeholder", fontSize = 10.sp, color = Color.Gray)
                }

                Spacer(modifier = Modifier.width(12.dp))

                // PART 3: Details (1/2 Right)
                Column(
                    modifier = Modifier.weight(1f).fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    DetailChip(listing.date)
                    DetailChip(listing.time)
                    DetailChip(listing.location)
                    DetailChip(listing.price, isPrice = true)
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketDetailScreen(ticket: TicketListing?,
                       onBack: () -> Unit,
                       userType: UserType,
                       onAddToCart: (TicketListing) -> Unit) {
    if (ticket == null) return // Safety check

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("${ticket.title}-${ticket.id}", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PickTickBlue)
            )
        },
        // ONLY show the bottom bar if the user is a Buyer
        bottomBar = {
            if (userType == UserType.BUYER) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color.White, // Clean background for the button area
                    shadowElevation = 8.dp
                ) {
                    Box(
                        modifier = Modifier
                            .padding(16.dp)
                            .navigationBarsPadding()
                    ) {
                        Button(
                            onClick = { onAddToCart(ticket) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = PickTickOrange
                            )
                        ) {
                            Text(
                                text = "Add to Shopping Cart",
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
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Proof Placeholder (Top Box)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color.LightGray, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text("Proof of Ticket (Image/Confirmation)")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Information List
            DetailRow("Event", ticket.eventName)
            DetailRow("Date/Time", "${ticket.date} @ ${ticket.time}")
            DetailRow("Location", ticket.location)
            DetailRow("Price", ticket.price)
            DetailRow("Category", ticket.category)
            DetailRow("Seat", ticket.seat)

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

            Text("Description", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(ticket.description, color = Color.Gray)
        }
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
fun UserProfileHeader(
    username: String,
    email: String,
    description: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.LightGray),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User Avatar",
                modifier = Modifier.size(70.dp),
                tint = Color.White
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Info
        Column {
            Text(
                text = username,
                style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            )
            Text(
                text = email,
                style = TextStyle(fontSize = 14.sp, color = Color.Gray)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = TextStyle(fontSize = 12.sp, color = Color.DarkGray, fontStyle = FontStyle.Italic)
            )
        }
    }
}

@Composable
fun SellerTicketHistory() {
    val historyTicket = TicketListing(
        id = "0193",
        title = "Nintendo Expo",
        eventName = "Nintendo Showcase of Dallas",
        date = "Nov 05",
        time = "10:00 AM",
        location = "Dallas, TX",
        price = "$50",
        category = "Expo",
        seat = "N/A",
        description = "Full access to all booths."
    )

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Ticket History",
            style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(bottom = 8.dp, start = 8.dp)
        )

        Box(modifier = Modifier.alpha(0.8f)) {
            TicketItem(listing = historyTicket, onClick = { /* History click */ })

            Text(
                text = "PAID",
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(20.dp)
                    .graphicsLayer(rotationZ = 15f),
                color = Color.Red.copy(alpha = 0.5f),
                fontWeight = FontWeight.Black,
                fontSize = 24.sp
            )
        }
    }
}
@Composable
fun UserProfileScreen(userType: UserType, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F8F8))
            .verticalScroll(rememberScrollState())
    ) {
        // 1. Show header for everyone
        UserProfileHeader(
            username = "Pter",
            email = "pxn0340@gmail.com",
            description = "Get to know me more at Ins:@PterPsleep"
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

        // 2. Only show history if the user is a SELLER
        if (userType == UserType.BUYER) {
            SellerTicketHistory()
        }
    }
}
