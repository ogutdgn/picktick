package com.example.picktick.data.crud

import com.example.picktick.data.DatabaseHelper
import android.content.ContentValues
import android.database.Cursor
import com.example.picktick.model.ChatThread
import com.example.picktick.model.Message
import com.example.picktick.model.MessageStatus
import java.util.Date

class Chat(private val db: DatabaseHelper) {

    fun insertThread(thread: ChatThread): Boolean {
        val values = ContentValues().apply {
            put(DatabaseHelper.THREAD_ID, thread.threadId)
            put(DatabaseHelper.THREAD_PARTICIPANT_IDS, thread.participantIds.joinToString(","))
            put(DatabaseHelper.THREAD_LAST_MESSAGE, thread.lastMessage)
            put(DatabaseHelper.THREAD_LAST_MESSAGE_TIME, thread.lastMessageTime.time)
        }
        return db.writableDatabase.insert(DatabaseHelper.TABLE_CHAT_THREADS, null, values) != -1L
    }

    fun updateThread(threadId: String, lastMessage: String, lastMessageTime: Date): Boolean {
        val values = ContentValues().apply {
            put(DatabaseHelper.THREAD_LAST_MESSAGE, lastMessage)
            put(DatabaseHelper.THREAD_LAST_MESSAGE_TIME, lastMessageTime.time)
        }
        return db.writableDatabase.update(
            DatabaseHelper.TABLE_CHAT_THREADS, values,
            "${DatabaseHelper.THREAD_ID} = ?", arrayOf(threadId)
        ) > 0
    }

    fun getThreadsForUser(userId: String): List<ChatThread> {
        val cursor = db.readableDatabase.query(
            DatabaseHelper.TABLE_CHAT_THREADS, null, null, null, null, null,
            "${DatabaseHelper.THREAD_LAST_MESSAGE_TIME} DESC"
        )
        return cursor.use { c ->
            val list = mutableListOf<ChatThread>()
            while (c.moveToNext()) {
                val thread = c.toThread()
                if (thread.participantIds.contains(userId)) list.add(thread)
            }
            list
        }
    }

    fun findThread(userId1: String, userId2: String): ChatThread? {
        val cursor = db.readableDatabase.query(
            DatabaseHelper.TABLE_CHAT_THREADS, null, null, null, null, null, null
        )
        return cursor.use { c ->
            var found: ChatThread? = null
            while (c.moveToNext()) {
                val thread = c.toThread()
                if (thread.participantIds.contains(userId1) && thread.participantIds.contains(userId2)) {
                    found = thread
                    break
                }
            }
            found
        }
    }

    fun insertMessage(message: Message): Boolean {
        val values = ContentValues().apply {
            put(DatabaseHelper.MESSAGE_ID, message.messageId)
            put(DatabaseHelper.MESSAGE_THREAD_ID, message.threadId)
            put(DatabaseHelper.MESSAGE_SENDER_ID, message.senderId)
            put(DatabaseHelper.MESSAGE_CONTENT, message.content)
            put(DatabaseHelper.MESSAGE_TIMESTAMP, message.timestamp.time)
            put(DatabaseHelper.MESSAGE_STATUS, message.status.name)
        }
        return db.writableDatabase.insert(DatabaseHelper.TABLE_MESSAGES, null, values) != -1L
    }

    fun getMessages(threadId: String): List<Message> {
        val cursor = db.readableDatabase.query(
            DatabaseHelper.TABLE_MESSAGES, null,
            "${DatabaseHelper.MESSAGE_THREAD_ID} = ?",
            arrayOf(threadId),
            null, null,
            "${DatabaseHelper.MESSAGE_TIMESTAMP} ASC"
        )
        return cursor.use { c ->
            val list = mutableListOf<Message>()
            while (c.moveToNext()) list.add(c.toMessage())
            list
        }
    }

    private fun Cursor.toThread(): ChatThread {
        val participantStr = getString(getColumnIndexOrThrow(DatabaseHelper.THREAD_PARTICIPANT_IDS))
        return ChatThread(
            threadId = getString(getColumnIndexOrThrow(DatabaseHelper.THREAD_ID)),
            participantIds = participantStr.split(","),
            lastMessage = getString(getColumnIndexOrThrow(DatabaseHelper.THREAD_LAST_MESSAGE)),
            lastMessageTime = Date(getLong(getColumnIndexOrThrow(DatabaseHelper.THREAD_LAST_MESSAGE_TIME)))
        )
    }

    private fun Cursor.toMessage(): Message {
        return Message(
            messageId = getString(getColumnIndexOrThrow(DatabaseHelper.MESSAGE_ID)),
            threadId = getString(getColumnIndexOrThrow(DatabaseHelper.MESSAGE_THREAD_ID)),
            senderId = getString(getColumnIndexOrThrow(DatabaseHelper.MESSAGE_SENDER_ID)),
            content = getString(getColumnIndexOrThrow(DatabaseHelper.MESSAGE_CONTENT)),
            timestamp = Date(getLong(getColumnIndexOrThrow(DatabaseHelper.MESSAGE_TIMESTAMP))),
            status = MessageStatus.valueOf(getString(getColumnIndexOrThrow(DatabaseHelper.MESSAGE_STATUS)))
        )
    }
}
