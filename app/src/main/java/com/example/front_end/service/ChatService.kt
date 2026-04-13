package com.example.front_end.service

import com.example.front_end.data.MockData
import com.example.front_end.model.ChatThread
import com.example.front_end.model.Message
import com.example.front_end.model.MessageStatus
import java.util.Date
import java.util.UUID

object ChatService {

    fun getThreadsForUser(userId: String): List<ChatThread> {
        return MockData.threads.filter { it.participantIds.contains(userId) }
    }

    fun getOrCreateThread(userId1: String, userId2: String): ChatThread {
        val existing = MockData.threads.find {
            it.participantIds.contains(userId1) && it.participantIds.contains(userId2)
        }
        if (existing != null) return existing

        val newThread = ChatThread(
            threadId = UUID.randomUUID().toString(),
            participantIds = listOf(userId1, userId2),
            lastMessage = "",
            lastMessageTime = Date()
        )
        MockData.threads.add(newThread)
        return newThread
    }

    fun getMessages(threadId: String): List<Message> {
        return MockData.messages.filter { it.threadId == threadId }
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
        MockData.messages.add(message)

        val threadIndex = MockData.threads.indexOfFirst { it.threadId == threadId }
        if (threadIndex != -1) {
            MockData.threads[threadIndex] = MockData.threads[threadIndex].copy(
                lastMessage = content,
                lastMessageTime = Date()
            )
        }
        return true
    }

    fun getOtherParticipantId(thread: ChatThread, currentUserId: String): String {
        return thread.participantIds.first { it != currentUserId }
    }
}
