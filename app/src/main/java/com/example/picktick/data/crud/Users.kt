package com.example.picktick.data.crud

import com.example.picktick.data.DatabaseHelper
import android.content.ContentValues
import android.database.Cursor
import com.example.picktick.model.User
import com.example.picktick.model.UserRole
import java.util.Date

class Users(private val db: DatabaseHelper) {

    fun insert(user: User): Boolean {
        val values = ContentValues().apply {
            put(DatabaseHelper.USER_ID, user.userId)
            put(DatabaseHelper.USER_NAME, user.name)
            put(DatabaseHelper.USER_EMAIL, user.email)
            put(DatabaseHelper.USER_PASSWORD, user.passwordHash)
            put(DatabaseHelper.USER_ROLE, user.role.name)
            put(DatabaseHelper.USER_IS_ONLINE, if (user.isOnline) 1 else 0)
            put(DatabaseHelper.USER_RATING, user.rating)
            put(DatabaseHelper.USER_REVIEW_COUNT, user.reviewCount)
            put(DatabaseHelper.USER_CREATED_AT, user.createdAt.time)
            put(DatabaseHelper.USER_IS_ACTIVE, if (user.isActive) 1 else 0)
            put(DatabaseHelper.USER_AVATAR_URL, user.avatarUrl)
        }
        return db.writableDatabase.insert(DatabaseHelper.TABLE_USERS, null, values) != -1L
    }

    fun findByEmailAndPassword(email: String, password: String): User? {
        val cursor = db.readableDatabase.query(
            DatabaseHelper.TABLE_USERS, null,
            "${DatabaseHelper.USER_EMAIL} = ? AND ${DatabaseHelper.USER_PASSWORD} = ? AND ${DatabaseHelper.USER_IS_ACTIVE} = 1",
            arrayOf(email, password),
            null, null, null
        )
        return cursor.use { if (it.moveToFirst()) it.toUser() else null }
    }

    fun findById(userId: String): User? {
        val cursor = db.readableDatabase.query(
            DatabaseHelper.TABLE_USERS, null,
            "${DatabaseHelper.USER_ID} = ?",
            arrayOf(userId),
            null, null, null
        )
        return cursor.use { if (it.moveToFirst()) it.toUser() else null }
    }

    fun findByEmail(email: String): User? {
        val cursor = db.readableDatabase.query(
            DatabaseHelper.TABLE_USERS, null,
            "${DatabaseHelper.USER_EMAIL} = ?",
            arrayOf(email),
            null, null, null
        )
        return cursor.use { if (it.moveToFirst()) it.toUser() else null }
    }

    fun getAll(): List<User> {
        val cursor = db.readableDatabase.query(
            DatabaseHelper.TABLE_USERS, null, null, null, null, null, null
        )
        return cursor.use { c ->
            val list = mutableListOf<User>()
            while (c.moveToNext()) list.add(c.toUser())
            list
        }
    }

    fun setActive(userId: String, isActive: Boolean): Boolean {
        val values = ContentValues().apply {
            put(DatabaseHelper.USER_IS_ACTIVE, if (isActive) 1 else 0)
        }
        return db.writableDatabase.update(
            DatabaseHelper.TABLE_USERS, values,
            "${DatabaseHelper.USER_ID} = ?", arrayOf(userId)
        ) > 0
    }

    fun updatePassword(email: String, newPassword: String): Boolean {
        val values = ContentValues().apply {
            put(DatabaseHelper.USER_PASSWORD, newPassword)
        }
        return db.writableDatabase.update(
            DatabaseHelper.TABLE_USERS, values,
            "${DatabaseHelper.USER_EMAIL} = ?", arrayOf(email)
        ) > 0
    }

    fun updateRating(userId: String, rating: Float, reviewCount: Int): Boolean {
        val values = ContentValues().apply {
            put(DatabaseHelper.USER_RATING, rating)
            put(DatabaseHelper.USER_REVIEW_COUNT, reviewCount)
        }
        return db.writableDatabase.update(
            DatabaseHelper.TABLE_USERS, values,
            "${DatabaseHelper.USER_ID} = ?", arrayOf(userId)
        ) > 0
    }

    private fun Cursor.toUser(): User {
        return User(
            userId = getString(getColumnIndexOrThrow(DatabaseHelper.USER_ID)),
            name = getString(getColumnIndexOrThrow(DatabaseHelper.USER_NAME)),
            email = getString(getColumnIndexOrThrow(DatabaseHelper.USER_EMAIL)),
            passwordHash = getString(getColumnIndexOrThrow(DatabaseHelper.USER_PASSWORD)),
            role = UserRole.valueOf(getString(getColumnIndexOrThrow(DatabaseHelper.USER_ROLE))),
            isOnline = getInt(getColumnIndexOrThrow(DatabaseHelper.USER_IS_ONLINE)) == 1,
            rating = getFloat(getColumnIndexOrThrow(DatabaseHelper.USER_RATING)),
            reviewCount = getInt(getColumnIndexOrThrow(DatabaseHelper.USER_REVIEW_COUNT)),
            createdAt = Date(getLong(getColumnIndexOrThrow(DatabaseHelper.USER_CREATED_AT))),
            isActive = getInt(getColumnIndexOrThrow(DatabaseHelper.USER_IS_ACTIVE)) == 1,
            avatarUrl = getString(getColumnIndexOrThrow(DatabaseHelper.USER_AVATAR_URL))
        )
    }
}
