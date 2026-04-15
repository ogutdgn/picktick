package com.example.picktick.data.crud

import com.example.picktick.data.DatabaseHelper
import android.content.ContentValues
import android.database.Cursor
import com.example.picktick.model.Order
import com.example.picktick.model.OrderStatus
import java.util.Date

class Orders(private val db: DatabaseHelper) {

    fun insert(order: Order): Boolean {
        val values = ContentValues().apply {
            put(DatabaseHelper.ORDER_ID, order.orderId)
            put(DatabaseHelper.ORDER_BUYER_ID, order.buyerId)
            put(DatabaseHelper.ORDER_SELLER_ID, order.sellerId)
            put(DatabaseHelper.ORDER_TICKET_ID, order.ticketId)
            put(DatabaseHelper.ORDER_TOTAL_PRICE, order.totalPrice)
            put(DatabaseHelper.ORDER_STATUS, order.status.name)
            put(DatabaseHelper.ORDER_CREATED_AT, order.createdAt.time)
        }
        return db.writableDatabase.insert(DatabaseHelper.TABLE_ORDERS, null, values) != -1L
    }

    fun getByBuyer(buyerId: String): List<Order> {
        val cursor = db.readableDatabase.query(
            DatabaseHelper.TABLE_ORDERS, null,
            "${DatabaseHelper.ORDER_BUYER_ID} = ?",
            arrayOf(buyerId),
            null, null,
            "${DatabaseHelper.ORDER_CREATED_AT} DESC"
        )
        return cursor.use { c ->
            val list = mutableListOf<Order>()
            while (c.moveToNext()) list.add(c.toOrder())
            list
        }
    }

    fun getBySeller(sellerId: String): List<Order> {
        val cursor = db.readableDatabase.query(
            DatabaseHelper.TABLE_ORDERS, null,
            "${DatabaseHelper.ORDER_SELLER_ID} = ?",
            arrayOf(sellerId),
            null, null,
            "${DatabaseHelper.ORDER_CREATED_AT} DESC"
        )
        return cursor.use { c ->
            val list = mutableListOf<Order>()
            while (c.moveToNext()) list.add(c.toOrder())
            list
        }
    }

    fun existsConfirmed(ticketId: String, buyerId: String): Boolean {
        val cursor = db.readableDatabase.query(
            DatabaseHelper.TABLE_ORDERS,
            arrayOf(DatabaseHelper.ORDER_ID),
            "${DatabaseHelper.ORDER_TICKET_ID} = ? AND ${DatabaseHelper.ORDER_BUYER_ID} = ? AND ${DatabaseHelper.ORDER_STATUS} = ?",
            arrayOf(ticketId, buyerId, OrderStatus.CONFIRMED.name),
            null, null, null
        )
        return cursor.use { it.moveToFirst() }
    }

    private fun Cursor.toOrder(): Order {
        return Order(
            orderId = getString(getColumnIndexOrThrow(DatabaseHelper.ORDER_ID)),
            buyerId = getString(getColumnIndexOrThrow(DatabaseHelper.ORDER_BUYER_ID)),
            sellerId = getString(getColumnIndexOrThrow(DatabaseHelper.ORDER_SELLER_ID)),
            ticketId = getString(getColumnIndexOrThrow(DatabaseHelper.ORDER_TICKET_ID)),
            totalPrice = getDouble(getColumnIndexOrThrow(DatabaseHelper.ORDER_TOTAL_PRICE)),
            status = OrderStatus.valueOf(getString(getColumnIndexOrThrow(DatabaseHelper.ORDER_STATUS))),
            createdAt = Date(getLong(getColumnIndexOrThrow(DatabaseHelper.ORDER_CREATED_AT)))
        )
    }
}
