package com.example.picktick.data.crud

import com.example.picktick.data.DatabaseHelper
import android.content.ContentValues
import android.database.Cursor
import com.example.picktick.model.Review
import java.util.Date

class Reviews(private val db: DatabaseHelper) {

    fun insert(review: Review): Boolean {
        val values = ContentValues().apply {
            put(DatabaseHelper.REVIEW_ID, review.reviewId)
            put(DatabaseHelper.REVIEW_REVIEWER_ID, review.reviewerId)
            put(DatabaseHelper.REVIEW_TARGET_ID, review.targetId)
            put(DatabaseHelper.REVIEW_RATING, review.rating)
            put(DatabaseHelper.REVIEW_CREATED_AT, review.createdAt.time)
        }
        return db.writableDatabase.insert(DatabaseHelper.TABLE_REVIEWS, null, values) != -1L
    }

    fun exists(reviewerId: String, targetId: String): Boolean {
        val cursor = db.readableDatabase.query(
            DatabaseHelper.TABLE_REVIEWS,
            arrayOf(DatabaseHelper.REVIEW_ID),
            "${DatabaseHelper.REVIEW_REVIEWER_ID} = ? AND ${DatabaseHelper.REVIEW_TARGET_ID} = ?",
            arrayOf(reviewerId, targetId),
            null, null, null
        )
        return cursor.use { it.moveToFirst() }
    }

    fun getByTarget(targetId: String): List<Review> {
        val cursor = db.readableDatabase.query(
            DatabaseHelper.TABLE_REVIEWS, null,
            "${DatabaseHelper.REVIEW_TARGET_ID} = ?",
            arrayOf(targetId),
            null, null, null
        )
        return cursor.use { c ->
            val list = mutableListOf<Review>()
            while (c.moveToNext()) list.add(c.toReview())
            list
        }
    }

    private fun Cursor.toReview(): Review {
        return Review(
            reviewId = getString(getColumnIndexOrThrow(DatabaseHelper.REVIEW_ID)),
            reviewerId = getString(getColumnIndexOrThrow(DatabaseHelper.REVIEW_REVIEWER_ID)),
            targetId = getString(getColumnIndexOrThrow(DatabaseHelper.REVIEW_TARGET_ID)),
            rating = getFloat(getColumnIndexOrThrow(DatabaseHelper.REVIEW_RATING)),
            createdAt = Date(getLong(getColumnIndexOrThrow(DatabaseHelper.REVIEW_CREATED_AT)))
        )
    }
}
