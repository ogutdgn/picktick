package com.example.front_end.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.front_end.model.User
import com.example.front_end.service.CartService

class AppState {
    var currentUser by mutableStateOf<User?>(null)
    var currentScreen by mutableStateOf<Screen>(Screen.Splash)

    var userSelectedTab by mutableStateOf(0)
    var adminSelectedTab by mutableStateOf(0)

    var filterMinPrice by mutableStateOf("0")
    var filterMaxPrice by mutableStateOf("9999")
    var filterStartDate by mutableStateOf("")
    var filterEndDate by mutableStateOf("")

    fun navigate(screen: Screen) {
        currentScreen = screen
    }

    fun logout() {
        currentUser = null
        CartService.clearCart()
        userSelectedTab = 0
        adminSelectedTab = 0
        currentScreen = Screen.Login
    }
}
