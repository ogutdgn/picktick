package com.example.front_end.service

import com.example.front_end.data.MockData
import com.example.front_end.model.Review
import java.util.Date
import java.util.UUID

object ReviewService {

    fun submitReview(reviewerId: String, targetId: String, rating: Float): Boolean {
        if (rating < 1f || rating > 5f) return false
        val alreadyReviewed = MockData.reviews.any {
            it.reviewerId == reviewerId && it.targetId == targetId
        }
        if (alreadyReviewed) return false

        val review = Review(
            reviewId = UUID.randomUUID().toString(),
            reviewerId = reviewerId,
            targetId = targetId,
            rating = rating,
            createdAt = Date()
        )
        MockData.reviews.add(review)
        updateUserRating(targetId)
        return true
    }

    fun hasUserReviewed(reviewerId: String, targetId: String): Boolean {
        return MockData.reviews.any {
            it.reviewerId == reviewerId && it.targetId == targetId
        }
    }

    fun getAverageRating(userId: String): Float {
        val reviews = MockData.reviews.filter { it.targetId == userId }
        if (reviews.isEmpty()) return 0f
        return reviews.map { it.rating }.average().toFloat()
    }

    fun getReviewCount(userId: String): Int {
        return MockData.reviews.count { it.targetId == userId }
    }

    private fun updateUserRating(userId: String) {
        val index = MockData.users.indexOfFirst { it.userId == userId }
        if (index == -1) return
        val avg = getAverageRating(userId)
        val count = getReviewCount(userId)
        MockData.users[index] = MockData.users[index].copy(rating = avg, reviewCount = count)
    }
}
