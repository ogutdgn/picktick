package com.example.picktick.model

import java.util.Date

data class ChatThread(
    val threadId: String,
    val participantIds: List<String>,
    val lastMessage: String = "",
    val lastMessageTime: Date = Date()
)
