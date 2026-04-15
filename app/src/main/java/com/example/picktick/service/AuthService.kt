package com.example.picktick.service

import com.example.picktick.data.DatabaseManager
import com.example.picktick.model.User
import com.example.picktick.model.UserRole
import java.util.Date
import java.util.UUID

object AuthService {

    fun login(email: String, password: String): User? {
        return DatabaseManager.users.findByEmailAndPassword(email, password)
    }

    fun register(name: String, email: String, password: String, role: UserRole): Boolean {
        if (DatabaseManager.users.findByEmail(email) != null) return false
        val newUser = User(
            userId = UUID.randomUUID().toString(),
            name = name,
            email = email,
            passwordHash = password,
            role = role,
            createdAt = Date(),
            isActive = true
        )
        return DatabaseManager.users.insert(newUser)
    }

    fun getUserById(userId: String): User? {
        return DatabaseManager.users.findById(userId)
    }

    fun isEmailRegistered(email: String): Boolean {
        return DatabaseManager.users.findByEmail(email) != null
    }

    fun isBanned(email: String): Boolean {
        val user = DatabaseManager.users.findByEmail(email)
        return user != null && !user.isActive
    }

    fun changePassword(email: String, newPassword: String): Boolean {
        return DatabaseManager.users.updatePassword(email, newPassword)
    }
}
