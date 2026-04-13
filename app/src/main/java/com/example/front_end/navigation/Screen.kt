package com.example.front_end.navigation

sealed class Screen {
    object Splash : Screen()
    object Login : Screen()
    object Register : Screen()
    object PasswordReset : Screen()
    object BuyerDashboard : Screen()
    object SellerDashboard : Screen()
    object AdminDashboard : Screen()
    data class TicketDetail(val ticketId: String) : Screen()
    object Cart : Screen()
    object Payment : Screen()
    object Profile : Screen()
    object ChatList : Screen()
    data class ChatThread(val threadId: String) : Screen()
    object SalesDashboard : Screen()
    object CreateListing : Screen()
    data class ModifyListing(val ticketId: String) : Screen()
}
