package com.example.picktick.data.crud

import com.example.picktick.data.DatabaseHelper
import android.content.ContentValues
import android.database.Cursor
import com.example.picktick.model.ListingStatus
import com.example.picktick.model.TicketListing
import java.util.Date

class Listings(private val db: DatabaseHelper) {

    fun insert(listing: TicketListing): Boolean {
        val values = ContentValues().apply {
            put(DatabaseHelper.LISTING_ID, listing.id)
            put(DatabaseHelper.LISTING_TITLE, listing.title)
            put(DatabaseHelper.LISTING_EVENT_NAME, listing.eventName)
            put(DatabaseHelper.LISTING_DATE, listing.date)
            put(DatabaseHelper.LISTING_TIME, listing.time)
            put(DatabaseHelper.LISTING_LOCATION, listing.location)
            put(DatabaseHelper.LISTING_PRICE, listing.price)
            put(DatabaseHelper.LISTING_CATEGORY, listing.category)
            put(DatabaseHelper.LISTING_SEAT, listing.seat)
            put(DatabaseHelper.LISTING_DESCRIPTION, listing.description)
            put(DatabaseHelper.LISTING_SELLER_ID, listing.sellerId)
            put(DatabaseHelper.LISTING_STATUS, listing.status.name)
            put(DatabaseHelper.LISTING_PROOF_CODE, listing.proofCode)
            put(DatabaseHelper.LISTING_CREATED_AT, listing.createdAt.time)
        }
        return db.writableDatabase.insert(DatabaseHelper.TABLE_LISTINGS, null, values) != -1L
    }

    fun getAll(): List<TicketListing> {
        val cursor = db.readableDatabase.query(
            DatabaseHelper.TABLE_LISTINGS, null, null, null, null, null,
            "${DatabaseHelper.LISTING_CREATED_AT} DESC"
        )
        return cursor.use { c ->
            val list = mutableListOf<TicketListing>()
            while (c.moveToNext()) list.add(c.toListing())
            list
        }
    }

    fun getAllActive(): List<TicketListing> {
        val cursor = db.readableDatabase.query(
            DatabaseHelper.TABLE_LISTINGS, null,
            "${DatabaseHelper.LISTING_STATUS} = ?",
            arrayOf(ListingStatus.ACTIVE.name),
            null, null,
            "${DatabaseHelper.LISTING_CREATED_AT} DESC"
        )
        return cursor.use { c ->
            val list = mutableListOf<TicketListing>()
            while (c.moveToNext()) list.add(c.toListing())
            list
        }
    }

    fun findById(id: String): TicketListing? {
        val cursor = db.readableDatabase.query(
            DatabaseHelper.TABLE_LISTINGS, null,
            "${DatabaseHelper.LISTING_ID} = ?",
            arrayOf(id),
            null, null, null
        )
        return cursor.use { if (it.moveToFirst()) it.toListing() else null }
    }

    fun findBySeller(sellerId: String): List<TicketListing> {
        val cursor = db.readableDatabase.query(
            DatabaseHelper.TABLE_LISTINGS, null,
            "${DatabaseHelper.LISTING_SELLER_ID} = ?",
            arrayOf(sellerId),
            null, null,
            "${DatabaseHelper.LISTING_CREATED_AT} DESC"
        )
        return cursor.use { c ->
            val list = mutableListOf<TicketListing>()
            while (c.moveToNext()) list.add(c.toListing())
            list
        }
    }

    fun update(listing: TicketListing): Boolean {
        val values = ContentValues().apply {
            put(DatabaseHelper.LISTING_TITLE, listing.title)
            put(DatabaseHelper.LISTING_EVENT_NAME, listing.eventName)
            put(DatabaseHelper.LISTING_DATE, listing.date)
            put(DatabaseHelper.LISTING_TIME, listing.time)
            put(DatabaseHelper.LISTING_LOCATION, listing.location)
            put(DatabaseHelper.LISTING_PRICE, listing.price)
            put(DatabaseHelper.LISTING_CATEGORY, listing.category)
            put(DatabaseHelper.LISTING_SEAT, listing.seat)
            put(DatabaseHelper.LISTING_DESCRIPTION, listing.description)
            put(DatabaseHelper.LISTING_STATUS, listing.status.name)
            put(DatabaseHelper.LISTING_PROOF_CODE, listing.proofCode)
        }
        return db.writableDatabase.update(
            DatabaseHelper.TABLE_LISTINGS, values,
            "${DatabaseHelper.LISTING_ID} = ?", arrayOf(listing.id)
        ) > 0
    }

    fun updateStatus(id: String, status: ListingStatus): Boolean {
        val values = ContentValues().apply {
            put(DatabaseHelper.LISTING_STATUS, status.name)
        }
        return db.writableDatabase.update(
            DatabaseHelper.TABLE_LISTINGS, values,
            "${DatabaseHelper.LISTING_ID} = ?", arrayOf(id)
        ) > 0
    }

    fun delete(id: String): Boolean {
        return db.writableDatabase.delete(
            DatabaseHelper.TABLE_LISTINGS,
            "${DatabaseHelper.LISTING_ID} = ?", arrayOf(id)
        ) > 0
    }

    private fun Cursor.toListing(): TicketListing {
        return TicketListing(
            id = getString(getColumnIndexOrThrow(DatabaseHelper.LISTING_ID)),
            title = getString(getColumnIndexOrThrow(DatabaseHelper.LISTING_TITLE)),
            eventName = getString(getColumnIndexOrThrow(DatabaseHelper.LISTING_EVENT_NAME)),
            date = getString(getColumnIndexOrThrow(DatabaseHelper.LISTING_DATE)),
            time = getString(getColumnIndexOrThrow(DatabaseHelper.LISTING_TIME)),
            location = getString(getColumnIndexOrThrow(DatabaseHelper.LISTING_LOCATION)),
            price = getDouble(getColumnIndexOrThrow(DatabaseHelper.LISTING_PRICE)),
            category = getString(getColumnIndexOrThrow(DatabaseHelper.LISTING_CATEGORY)),
            seat = getString(getColumnIndexOrThrow(DatabaseHelper.LISTING_SEAT)),
            description = getString(getColumnIndexOrThrow(DatabaseHelper.LISTING_DESCRIPTION)),
            sellerId = getString(getColumnIndexOrThrow(DatabaseHelper.LISTING_SELLER_ID)),
            status = ListingStatus.valueOf(getString(getColumnIndexOrThrow(DatabaseHelper.LISTING_STATUS))),
            proofCode = getString(getColumnIndexOrThrow(DatabaseHelper.LISTING_PROOF_CODE)),
            createdAt = Date(getLong(getColumnIndexOrThrow(DatabaseHelper.LISTING_CREATED_AT)))
        )
    }
}
