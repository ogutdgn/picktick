package com.example.picktick.data.crud

import com.example.picktick.data.DatabaseHelper
import android.content.ContentValues
import android.database.Cursor
import com.example.picktick.model.CartItem
import java.util.Date

class Carts(private val db: DatabaseHelper) {

    fun insert(item: CartItem): Boolean {
        val values = ContentValues().apply {
            put(DatabaseHelper.CART_ITEM_ID, item.cartItemId)
            put(DatabaseHelper.CART_USER_ID, item.userId)
            put(DatabaseHelper.CART_TICKET_ID, item.ticketId)
            put(DatabaseHelper.CART_ADDED_AT, item.addedAt.time)
        }
        return db.writableDatabase.insert(DatabaseHelper.TABLE_CART_ITEMS, null, values) != -1L
    }

    fun getByUser(userId: String): List<CartItem> {
        val cursor = db.readableDatabase.query(
            DatabaseHelper.TABLE_CART_ITEMS, null,
            "${DatabaseHelper.CART_USER_ID} = ?",
            arrayOf(userId),
            null, null, null
        )
        return cursor.use { c ->
            val list = mutableListOf<CartItem>()
            while (c.moveToNext()) list.add(c.toCartItem())
            list
        }
    }

    fun delete(ticketId: String, userId: String): Boolean {
        return db.writableDatabase.delete(
            DatabaseHelper.TABLE_CART_ITEMS,
            "${DatabaseHelper.CART_TICKET_ID} = ? AND ${DatabaseHelper.CART_USER_ID} = ?",
            arrayOf(ticketId, userId)
        ) > 0
    }

    fun deleteByUser(userId: String) {
        db.writableDatabase.delete(
            DatabaseHelper.TABLE_CART_ITEMS,
            "${DatabaseHelper.CART_USER_ID} = ?",
            arrayOf(userId)
        )
    }

    fun deleteAll() {
        db.writableDatabase.delete(DatabaseHelper.TABLE_CART_ITEMS, null, null)
    }

    fun exists(ticketId: String, userId: String): Boolean {
        val cursor = db.readableDatabase.query(
            DatabaseHelper.TABLE_CART_ITEMS,
            arrayOf(DatabaseHelper.CART_ITEM_ID),
            "${DatabaseHelper.CART_TICKET_ID} = ? AND ${DatabaseHelper.CART_USER_ID} = ?",
            arrayOf(ticketId, userId),
            null, null, null
        )
        return cursor.use { it.moveToFirst() }
    }

    private fun Cursor.toCartItem(): CartItem {
        return CartItem(
            cartItemId = getString(getColumnIndexOrThrow(DatabaseHelper.CART_ITEM_ID)),
            userId = getString(getColumnIndexOrThrow(DatabaseHelper.CART_USER_ID)),
            ticketId = getString(getColumnIndexOrThrow(DatabaseHelper.CART_TICKET_ID)),
            addedAt = Date(getLong(getColumnIndexOrThrow(DatabaseHelper.CART_ADDED_AT)))
        )
    }
}
