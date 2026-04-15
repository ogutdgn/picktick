package com.example.picktick.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "picktick.db"
        const val DATABASE_VERSION = 1

        const val TABLE_USERS = "users"
        const val USER_ID = "user_id"
        const val USER_NAME = "name"
        const val USER_EMAIL = "email"
        const val USER_PASSWORD = "password_hash"
        const val USER_ROLE = "role"
        const val USER_IS_ONLINE = "is_online"
        const val USER_RATING = "rating"
        const val USER_REVIEW_COUNT = "review_count"
        const val USER_CREATED_AT = "created_at"
        const val USER_IS_ACTIVE = "is_active"
        const val USER_AVATAR_URL = "avatar_url"

        const val TABLE_LISTINGS = "listings"
        const val LISTING_ID = "listing_id"
        const val LISTING_TITLE = "title"
        const val LISTING_EVENT_NAME = "event_name"
        const val LISTING_DATE = "date"
        const val LISTING_TIME = "time"
        const val LISTING_LOCATION = "location"
        const val LISTING_PRICE = "price"
        const val LISTING_CATEGORY = "category"
        const val LISTING_SEAT = "seat"
        const val LISTING_DESCRIPTION = "description"
        const val LISTING_SELLER_ID = "seller_id"
        const val LISTING_STATUS = "status"
        const val LISTING_PROOF_CODE = "proof_code"
        const val LISTING_CREATED_AT = "created_at"

        const val TABLE_CART_ITEMS = "cart_items"
        const val CART_ITEM_ID = "cart_item_id"
        const val CART_USER_ID = "user_id"
        const val CART_TICKET_ID = "ticket_id"
        const val CART_ADDED_AT = "added_at"

        const val TABLE_ORDERS = "orders"
        const val ORDER_ID = "order_id"
        const val ORDER_BUYER_ID = "buyer_id"
        const val ORDER_SELLER_ID = "seller_id"
        const val ORDER_TICKET_ID = "ticket_id"
        const val ORDER_TOTAL_PRICE = "total_price"
        const val ORDER_STATUS = "status"
        const val ORDER_CREATED_AT = "created_at"

        const val TABLE_MESSAGES = "messages"
        const val MESSAGE_ID = "message_id"
        const val MESSAGE_THREAD_ID = "thread_id"
        const val MESSAGE_SENDER_ID = "sender_id"
        const val MESSAGE_CONTENT = "content"
        const val MESSAGE_TIMESTAMP = "timestamp"
        const val MESSAGE_STATUS = "status"

        const val TABLE_CHAT_THREADS = "chat_threads"
        const val THREAD_ID = "thread_id"
        const val THREAD_PARTICIPANT_IDS = "participant_ids"
        const val THREAD_LAST_MESSAGE = "last_message"
        const val THREAD_LAST_MESSAGE_TIME = "last_message_time"

        const val TABLE_REVIEWS = "reviews"
        const val REVIEW_ID = "review_id"
        const val REVIEW_REVIEWER_ID = "reviewer_id"
        const val REVIEW_TARGET_ID = "target_id"
        const val REVIEW_RATING = "rating"
        const val REVIEW_CREATED_AT = "created_at"
    }

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE $TABLE_USERS (
                $USER_ID TEXT PRIMARY KEY,
                $USER_NAME TEXT NOT NULL,
                $USER_EMAIL TEXT NOT NULL UNIQUE,
                $USER_PASSWORD TEXT NOT NULL,
                $USER_ROLE TEXT NOT NULL,
                $USER_IS_ONLINE INTEGER NOT NULL DEFAULT 0,
                $USER_RATING REAL NOT NULL DEFAULT 0,
                $USER_REVIEW_COUNT INTEGER NOT NULL DEFAULT 0,
                $USER_CREATED_AT INTEGER NOT NULL,
                $USER_IS_ACTIVE INTEGER NOT NULL DEFAULT 1,
                $USER_AVATAR_URL TEXT
            )
        """)

        db.execSQL("""
            CREATE TABLE $TABLE_LISTINGS (
                $LISTING_ID TEXT PRIMARY KEY,
                $LISTING_TITLE TEXT NOT NULL,
                $LISTING_EVENT_NAME TEXT NOT NULL,
                $LISTING_DATE TEXT NOT NULL,
                $LISTING_TIME TEXT NOT NULL,
                $LISTING_LOCATION TEXT NOT NULL,
                $LISTING_PRICE REAL NOT NULL,
                $LISTING_CATEGORY TEXT NOT NULL,
                $LISTING_SEAT TEXT NOT NULL,
                $LISTING_DESCRIPTION TEXT NOT NULL,
                $LISTING_SELLER_ID TEXT NOT NULL,
                $LISTING_STATUS TEXT NOT NULL,
                $LISTING_PROOF_CODE TEXT NOT NULL,
                $LISTING_CREATED_AT INTEGER NOT NULL
            )
        """)

        db.execSQL("""
            CREATE TABLE $TABLE_CART_ITEMS (
                $CART_ITEM_ID TEXT PRIMARY KEY,
                $CART_USER_ID TEXT NOT NULL,
                $CART_TICKET_ID TEXT NOT NULL,
                $CART_ADDED_AT INTEGER NOT NULL
            )
        """)

        db.execSQL("""
            CREATE TABLE $TABLE_ORDERS (
                $ORDER_ID TEXT PRIMARY KEY,
                $ORDER_BUYER_ID TEXT NOT NULL,
                $ORDER_SELLER_ID TEXT NOT NULL,
                $ORDER_TICKET_ID TEXT NOT NULL,
                $ORDER_TOTAL_PRICE REAL NOT NULL,
                $ORDER_STATUS TEXT NOT NULL,
                $ORDER_CREATED_AT INTEGER NOT NULL
            )
        """)

        db.execSQL("""
            CREATE TABLE $TABLE_MESSAGES (
                $MESSAGE_ID TEXT PRIMARY KEY,
                $MESSAGE_THREAD_ID TEXT NOT NULL,
                $MESSAGE_SENDER_ID TEXT NOT NULL,
                $MESSAGE_CONTENT TEXT NOT NULL,
                $MESSAGE_TIMESTAMP INTEGER NOT NULL,
                $MESSAGE_STATUS TEXT NOT NULL
            )
        """)

        db.execSQL("""
            CREATE TABLE $TABLE_CHAT_THREADS (
                $THREAD_ID TEXT PRIMARY KEY,
                $THREAD_PARTICIPANT_IDS TEXT NOT NULL,
                $THREAD_LAST_MESSAGE TEXT NOT NULL DEFAULT '',
                $THREAD_LAST_MESSAGE_TIME INTEGER NOT NULL
            )
        """)

        db.execSQL("""
            CREATE TABLE $TABLE_REVIEWS (
                $REVIEW_ID TEXT PRIMARY KEY,
                $REVIEW_REVIEWER_ID TEXT NOT NULL,
                $REVIEW_TARGET_ID TEXT NOT NULL,
                $REVIEW_RATING REAL NOT NULL,
                $REVIEW_CREATED_AT INTEGER NOT NULL
            )
        """)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LISTINGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CART_ITEMS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_ORDERS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_MESSAGES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CHAT_THREADS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_REVIEWS")
        onCreate(db)
    }
}
