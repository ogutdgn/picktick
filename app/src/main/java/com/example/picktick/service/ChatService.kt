package com.example.picktick.service

import com.example.picktick.data.DatabaseManager
import com.example.picktick.model.ChatThread
import com.example.picktick.model.Message
import com.example.picktick.model.MessageStatus
import java.util.Date
import java.util.UUID

object ChatService {

    fun getThreadsForUser(userId: String): List<ChatThread> {
        return DatabaseManager.chat.getThreadsForUser(userId)
    }

    fun getOrCreateThread(userId1: String, userId2: String): ChatThread {
        val existing = DatabaseManager.chat.findThread(userId1, userId2)
        if (existing != null) return existing
        val newThread = ChatThread(
            threadId = UUID.randomUUID().toString(),
            participantIds = listOf(userId1, userId2),
            lastMessage = "",
            lastMessageTime = Date()
        )
        DatabaseManager.chat.insertThread(newThread)
        return newThread
    }

    fun getMessages(threadId: String): List<Message> {
        return DatabaseManager.chat.getMessages(threadId)
    }

    fun sendMessage(threadId: String, senderId: String, content: String): Boolean {
        if (content.isBlank()) return false
        val message = Message(
            messageId = UUID.randomUUID().toString(),
            threadId = threadId,
            senderId = senderId,
            content = content,
            timestamp = Date(),
            status = MessageStatus.SENT
        )
        DatabaseManager.chat.insertMessage(message)
        DatabaseManager.chat.updateThread(threadId, content, Date())
        return true
    }

    fun getOtherParticipantId(thread: ChatThread, currentUserId: String): String {
        return thread.participantIds.first { it != currentUserId }
    }
}
