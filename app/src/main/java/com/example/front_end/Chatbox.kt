package com.example.front_end

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.front_end.model.UserRole
import com.example.front_end.navigation.AppState
import com.example.front_end.navigation.Screen
import com.example.front_end.service.AuthService
import com.example.front_end.service.ChatService
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ChatListContent(appState: AppState) {
    val userId = appState.currentUser?.userId ?: ""
    var threads by remember { mutableStateOf(ChatService.getThreadsForUser(userId)) }
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    if (threads.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize().background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, modifier = Modifier.size(72.dp), tint = Color.LightGray)
                Text("No messages yet", fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                Text("Start a conversation from a user's profile.", fontSize = 13.sp, color = Color.LightGray, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp))
            }
        }
    } else {
        LazyColumn(modifier = Modifier.fillMaxSize().background(Color.White)) {
            items(threads) { thread ->
                val otherId = ChatService.getOtherParticipantId(thread, userId)
                val otherUser = remember { AuthService.getUserById(otherId) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { appState.navigate(Screen.ChatThread(thread.threadId)) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier.size(52.dp).clip(CircleShape).background(PickTickBlue),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = (otherUser?.name?.firstOrNull() ?: "?").toString().uppercase(),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(otherUser?.name ?: "Unknown", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text(timeFormat.format(thread.lastMessageTime), fontSize = 11.sp, color = Color.LightGray)
                        }
                        Text(
                            thread.lastMessage.ifBlank { "No messages yet" },
                            fontSize = 13.sp,
                            color = Color.Gray,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(start = 80.dp), color = Color(0xFFF0F0F0))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatThreadScreen(appState: AppState, threadId: String) {
    val userId = appState.currentUser?.userId ?: ""
    val isAdmin = appState.currentUser?.role == UserRole.ADMIN

    val thread = remember { ChatService.getThreadsForUser(userId).find { it.threadId == threadId } }
    val otherId = remember(thread) { thread?.let { ChatService.getOtherParticipantId(it, userId) } ?: "" }
    val otherUser = remember { AuthService.getUserById(otherId) }

    var messages by remember { mutableStateOf(ChatService.getMessages(threadId)) }
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(36.dp).clip(CircleShape).background(PickTickOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = (otherUser?.name?.firstOrNull() ?: "?").toString().uppercase(),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(otherUser?.name ?: "Chat", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text(
                                otherUser?.role?.name?.lowercase()?.replaceFirstChar { it.uppercase() } ?: "",
                                color = Color.White.copy(alpha = 0.75f),
                                fontSize = 11.sp
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        if (isAdmin) {
                            appState.adminSelectedTab = 2
                            appState.navigate(Screen.AdminDashboard)
                        } else {
                            appState.userSelectedTab = 3
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
            Surface(modifier = Modifier.fillMaxWidth(), color = Color.White, shadowElevation = 6.dp) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).navigationBarsPadding(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Type a message...") },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PickTickBlue,
                            unfocusedBorderColor = Color(0xFFDDDDDD)
                        ),
                        maxLines = 3
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                ChatService.sendMessage(threadId, userId, inputText.trim())
                                messages = ChatService.getMessages(threadId)
                                inputText = ""
                            }
                        },
                        modifier = Modifier.size(48.dp).clip(CircleShape).background(PickTickBlue)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                }
            }
        }
    ) { padding ->
        if (messages.isEmpty()) {
            Box(
                modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF5F5F5)),
                contentAlignment = Alignment.Center
            ) {
                Text("Send a message to start the conversation.", color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.padding(32.dp))
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.padding(padding).fillMaxSize().background(Color(0xFFF5F5F5)),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(messages) { message ->
                    val isMe = message.senderId == userId
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = if (isMe) Alignment.End else Alignment.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .widthIn(max = 280.dp)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 16.dp,
                                        topEnd = 16.dp,
                                        bottomStart = if (isMe) 16.dp else 4.dp,
                                        bottomEnd = if (isMe) 4.dp else 16.dp
                                    )
                                )
                                .background(if (isMe) PickTickBlue else Color.White)
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Text(
                                text = message.content,
                                color = if (isMe) Color.White else Color(0xFF222222),
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = timeFormat.format(message.timestamp),
                            fontSize = 10.sp,
                            color = Color.LightGray,
                            modifier = Modifier.padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
