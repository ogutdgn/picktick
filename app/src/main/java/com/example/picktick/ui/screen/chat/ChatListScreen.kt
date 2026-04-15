package com.example.picktick.ui.screen.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
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
import com.example.picktick.PickTickBlue
import com.example.picktick.navigation.AppState
import com.example.picktick.navigation.Screen
import com.example.picktick.service.AuthService
import com.example.picktick.service.ChatService
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun ChatListScreen(appState: AppState) {
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
