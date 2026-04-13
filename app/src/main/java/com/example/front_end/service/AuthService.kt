package com.example.front_end.service

import com.example.front_end.data.MockData
import com.example.front_end.model.User
import com.example.front_end.model.UserRole
import java.util.Date
import java.util.UUID

object AuthService {

    fun login(email: String, password: String): User? {
        return MockData.users.find {
            it.email == email && it.passwordHash == password && it.isActive
        }
    }

    fun register(name: String, email: String, password: String, role: UserRole): Boolean {
        val emailExists = MockData.users.any { it.email == email }
        if (emailExists) return false

        val newUser = User(
            userId = UUID.randomUUID().toString(),
            name = name,
            email = email,
            passwordHash = password,
            role = role,
            createdAt = Date(),
            isActive = true
        )
        MockData.users.add(newUser)
        return true
    }

    fun getUserById(userId: String): User? {
        return MockData.users.find { it.userId == userId }
    }
}
