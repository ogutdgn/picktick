package com.example.picktick.service

import com.example.picktick.data.DatabaseManager
import com.example.picktick.model.Review
import java.util.Date
import java.util.UUID

object ReviewService {

    fun submitReview(reviewerId: String, targetId: String, rating: Float): Boolean {
        if (rating < 1f || rating > 5f) return false
        if (DatabaseManager.reviews.exists(reviewerId, targetId)) return false
        val review = Review(
            reviewId = UUID.randomUUID().toString(),
            reviewerId = reviewerId,
            targetId = targetId,
            rating = rating,
            createdAt = Date()
        )
        DatabaseManager.reviews.insert(review)
        updateUserRating(targetId)
        return true
    }

    fun hasUserReviewed(reviewerId: String, targetId: String): Boolean {
        return DatabaseManager.reviews.exists(reviewerId, targetId)
    }

    fun getAverageRating(userId: String): Float {
        val reviews = DatabaseManager.reviews.getByTarget(userId)
        if (reviews.isEmpty()) return 0f
        return reviews.map { it.rating }.average().toFloat()
    }

    fun getReviewCount(userId: String): Int {
        return DatabaseManager.reviews.getByTarget(userId).size
    }

    fun getByTarget(userId: String) = DatabaseManager.reviews.getByTarget(userId)

    private fun updateUserRating(userId: String) {
        val avg = getAverageRating(userId)
        val count = getReviewCount(userId)
        DatabaseManager.users.updateRating(userId, avg, count)
    }
}
