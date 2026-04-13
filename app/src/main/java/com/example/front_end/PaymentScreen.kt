package com.example.front_end

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.front_end.navigation.AppState
import com.example.front_end.navigation.Screen
import com.example.front_end.service.CartService
import com.example.front_end.service.OrderService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(appState: AppState) {
    val userId = appState.currentUser?.userId ?: ""
    val cartListings = remember { CartService.getCartListings(userId) }
    val total = remember { CartService.getTotal(userId) }

    var selectedMethod by remember { mutableStateOf("Credit Card") }
    var cardNumber by remember { mutableStateOf("") }
    var cardHolder by remember { mutableStateOf("") }
    var expiryDate by remember { mutableStateOf(TextFieldValue("")) }
    var cvv by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var orderConfirmed by remember { mutableStateOf(false) }

    if (orderConfirmed) {
        PaymentSuccessScreen(appState = appState, total = total, itemCount = cartListings.size)
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checkout", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = {
                        appState.userSelectedTab = 2
                        appState.navigate(Screen.UserDashboard)
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PickTickBlue)
            )
        },
        bottomBar = {
            Surface(modifier = Modifier.fillMaxWidth(), color = Color.White, shadowElevation = 8.dp) {
                Column(modifier = Modifier.padding(16.dp).navigationBarsPadding()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Total", fontSize = 16.sp, color = Color.Gray)
                        Text(
                            "$${"%.2f".format(total)}",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = PickTickBlue
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    if (errorMessage.isNotEmpty()) {
                        Text(errorMessage, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(bottom = 6.dp))
                    }
                    Button(
                        onClick = {
                            when {
                                cartListings.isEmpty() -> errorMessage = "Your cart is empty."
                                selectedMethod == "Credit Card" && cardNumber.length < 16 -> errorMessage = "Enter a valid 16-digit card number."
                                selectedMethod == "Credit Card" && cardHolder.isBlank() -> errorMessage = "Enter the card holder name."
                                selectedMethod == "Credit Card" && expiryDate.text.isBlank() -> errorMessage = "Enter the expiry date."
                                selectedMethod == "Credit Card" && cvv.length < 3 -> errorMessage = "Enter a valid CVV."
                                else -> {
                                    OrderService.createOrders(userId, cartListings)
                                    CartService.clearCart(userId)
                                    orderConfirmed = true
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(54.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PickTickOrange)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Confirm Purchase", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text("Order Summary", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))

            Card(
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    cartListings.forEach { ticket ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(ticket.title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                Text("${ticket.date}  •  ${ticket.seat}", fontSize = 12.sp, color = Color.Gray)
                            }
                            Text("$${ticket.price.toInt()}", fontWeight = FontWeight.Bold, color = PickTickOrange)
                        }
                        if (ticket != cartListings.last()) {
                            HorizontalDivider(color = Color(0xFFEEEEEE))
                        }
                    }
                }
            }

            Text("Payment Method", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                PaymentMethodChip(
                    label = "Credit Card",
                    icon = Icons.Default.CreditCard,
                    selected = selectedMethod == "Credit Card",
                    onClick = { selectedMethod = "Credit Card"; errorMessage = "" },
                    modifier = Modifier.weight(1f)
                )
                PaymentMethodChip(
                    label = "PayPal",
                    icon = Icons.Default.AccountBalance,
                    selected = selectedMethod == "PayPal",
                    onClick = { selectedMethod = "PayPal"; errorMessage = "" },
                    modifier = Modifier.weight(1f)
                )
            }

            if (selectedMethod == "Credit Card") {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = cardNumber,
                            onValueChange = {
                                if (it.all { c -> c.isDigit() } && it.length <= 16) {
                                    cardNumber = it
                                    errorMessage = ""
                                }
                            },
                            label = { Text("Card Number") },
                            placeholder = { Text("1234 5678 9012 3456") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
                            shape = RoundedCornerShape(10.dp)
                        )
                        OutlinedTextField(
                            value = cardHolder,
                            onValueChange = { cardHolder = it; errorMessage = "" },
                            label = { Text("Card Holder Name") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            shape = RoundedCornerShape(10.dp)
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = expiryDate,
                                onValueChange = { input ->
                                    val digits = input.text.filter { it.isDigit() }.take(4)
                                    val formatted = if (digits.length >= 3) "${digits.take(2)}/${digits.drop(2)}" else digits
                                    expiryDate = TextFieldValue(formatted, selection = TextRange(formatted.length))
                                    errorMessage = ""
                                },
                                label = { Text("MM/YY") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                            OutlinedTextField(
                                value = cvv,
                                onValueChange = {
                                    if (it.all { c -> c.isDigit() } && it.length <= 3) {
                                        cvv = it
                                        errorMessage = ""
                                    }
                                },
                                label = { Text("CVV") },
                                modifier = Modifier.weight(1f),
                                singleLine = true,
                                shape = RoundedCornerShape(10.dp)
                            )
                        }
                    }
                }
            } else {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(2.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                        Text("You will be redirected to PayPal to complete the payment.", color = Color.Gray, textAlign = TextAlign.Center)
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodChip(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val borderColor = if (selected) PickTickBlue else Color(0xFFDDDDDD)
    val bgColor = if (selected) PickTickBlue.copy(alpha = 0.08f) else Color.White

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(bgColor)
            .border(2.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Icon(icon, contentDescription = null, tint = if (selected) PickTickBlue else Color.Gray, modifier = Modifier.size(20.dp))
            Text(label, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal, color = if (selected) PickTickBlue else Color.Gray, fontSize = 14.sp)
        }
    }
}

@Composable
fun PaymentSuccessScreen(appState: AppState, total: Double, itemCount: Int) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .background(Color(0xFF4CAF50).copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50),
                    modifier = Modifier.size(60.dp)
                )
            }

            Text("Payment Successful!", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF333333))
            Text(
                "$itemCount ticket${if (itemCount > 1) "s" else ""} purchased for $${"%.2f".format(total)}",
                fontSize = 15.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )
            Text(
                "Your tickets are confirmed. Check your profile for purchase history.",
                fontSize = 13.sp,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    appState.userSelectedTab = 0
                    appState.navigate(Screen.UserDashboard)
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PickTickBlue)
            ) {
                Text("Back to Marketplace", color = Color.White, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = {
                    appState.userSelectedTab = -1
                    appState.navigate(Screen.UserDashboard)
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("View Purchase History", color = PickTickBlue, fontWeight = FontWeight.Bold)
            }
        }
    }
}
