package com.example.picktick.model

import java.util.Date

enum class MessageStatus { SENT, DELIVERED }

data class Message(
    val messageId: String,
    val threadId: String,
    val senderId: String,
    val content: String,
    val timestamp: Date = Date(),
    val status: MessageStatus = MessageStatus.SENT
)
