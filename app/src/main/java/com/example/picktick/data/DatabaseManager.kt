package com.example.picktick.data

import android.content.Context
import com.example.picktick.data.crud.Users
import com.example.picktick.data.crud.Listings
import com.example.picktick.data.crud.Carts
import com.example.picktick.data.crud.Orders
import com.example.picktick.data.crud.Chat
import com.example.picktick.data.crud.Reviews

object DatabaseManager {

    private lateinit var db: DatabaseHelper

    fun init(context: Context) {
        db = DatabaseHelper(context.applicationContext)
    }

    val users get() = Users(db)
    val listings get() = Listings(db)
    val carts get() = Carts(db)
    val orders get() = Orders(db)
    val chat get() = Chat(db)
    val reviews get() = Reviews(db)
}
