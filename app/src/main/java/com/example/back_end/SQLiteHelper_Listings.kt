package com.example.picktick

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.provider.BaseColumns
import android.content.Context
import android.content.ContentValues

object LISTING_DB : BaseColumns {
    const val LISTING_DATA_BASE_NAME = "listing_db"
    const val TABLE_NAME = "listings"
    const val TICKET_NAME_SUB = "ticket_name"
    const val EVENT_NAME_SUB = "event_name"
    const val DATE_NAME_SUB = "event_date"
    const val LOCATION_NAME_SUB = "location"
    const val CATEGORY_NAME_SUB = "category"
    const val SEAT_NAME_SUB = "seat_number"
    const val IMAGE_NAME_SUB = "image_path"
    const val USER_NAME_SUB = "user_id"
    const val DESCRIPTION_NAME_SUB = "description"
}

class SQLiteHelper_Listings(context: Context) : SQLiteOpenHelper(context, LISTING_DB.LISTING_DATA_BASE_NAME, null, 2) {

    private val SQL_CREATE_LISTINGS = """
        CREATE TABLE ${LISTING_DB.TABLE_NAME} (
            ${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT,
            ${LISTING_DB.TICKET_NAME_SUB} TEXT NOT NULL,
            ${LISTING_DB.EVENT_NAME_SUB} TEXT NOT NULL,
            ${LISTING_DB.DATE_NAME_SUB} TEXT,
            ${LISTING_DB.LOCATION_NAME_SUB} TEXT,
            ${LISTING_DB.CATEGORY_NAME_SUB} TEXT,
            ${LISTING_DB.SEAT_NAME_SUB} TEXT,
            ${LISTING_DB.IMAGE_NAME_SUB} TEXT,
            ${LISTING_DB.USER_NAME_SUB} INTEGER,
            ${LISTING_DB.DESCRIPTION_NAME_SUB} TEXT
        )"""

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_LISTINGS)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${LISTING_DB.TABLE_NAME}")
        onCreate(db)
    }

    // Add a new listing, returns the new row ID or -1 if failed
    fun addListing(
        ticket: String,
        event: String,
        date: String,
        location: String,
        category: String,
        seat: String,
        image: String,
        userId: Int,
        description: String
    ): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(LISTING_DB.TICKET_NAME_SUB, ticket)
            put(LISTING_DB.EVENT_NAME_SUB, event)
            put(LISTING_DB.DATE_NAME_SUB, date)
            put(LISTING_DB.LOCATION_NAME_SUB, location)
            put(LISTING_DB.CATEGORY_NAME_SUB, category)
            put(LISTING_DB.SEAT_NAME_SUB, seat)
            put(LISTING_DB.IMAGE_NAME_SUB, image)
            put(LISTING_DB.USER_NAME_SUB, userId)
            put(LISTING_DB.DESCRIPTION_NAME_SUB, description)
        }
        return db.insert(LISTING_DB.TABLE_NAME, null, values)
    }

    // Get a single listing by its ID
    fun get_listing_from_id(listingId: Int): Map<String, String?>? {
        val db = readableDatabase

        val cursor = db.query(
            LISTING_DB.TABLE_NAME,
            null,
            "${BaseColumns._ID} = ?",
            arrayOf(listingId.toString()),
            null, null, null
        )

        if (!cursor.moveToFirst()) {
            cursor.close()
            return null
        }

        val listing = mapOf<String, String?>(
            "id" to cursor.getString(cursor.getColumnIndexOrThrow(BaseColumns._ID)),
            "ticket" to cursor.getString(cursor.getColumnIndexOrThrow(LISTING_DB.TICKET_NAME_SUB)),
            "event" to cursor.getString(cursor.getColumnIndexOrThrow(LISTING_DB.EVENT_NAME_SUB)),
            "date" to cursor.getString(cursor.getColumnIndexOrThrow(LISTING_DB.DATE_NAME_SUB)),
            "location" to cursor.getString(cursor.getColumnIndexOrThrow(LISTING_DB.LOCATION_NAME_SUB)),
            "category" to cursor.getString(cursor.getColumnIndexOrThrow(LISTING_DB.CATEGORY_NAME_SUB)),
            "seat" to cursor.getString(cursor.getColumnIndexOrThrow(LISTING_DB.SEAT_NAME_SUB)),
            "image" to cursor.getString(cursor.getColumnIndexOrThrow(LISTING_DB.IMAGE_NAME_SUB)),
            "description" to cursor.getString(cursor.getColumnIndexOrThrow(LISTING_DB.DESCRIPTION_NAME_SUB))
        )

        cursor.close()
        return listing
    }
}