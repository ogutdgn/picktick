package com.example.picktick.navigation

sealed class Screen {
    object Splash : Screen()
    object Login : Screen()
    object Register : Screen()
    object PasswordReset : Screen()
    object UserDashboard : Screen()
    object AdminDashboard : Screen()
    data class TicketDetail(val ticketId: String) : Screen()
    object Payment : Screen()
    object CreateListing : Screen()
    data class ModifyListing(val ticketId: String) : Screen()
    data class ChatThread(val threadId: String) : Screen()
    data class PublicProfile(val userId: String) : Screen()
}
