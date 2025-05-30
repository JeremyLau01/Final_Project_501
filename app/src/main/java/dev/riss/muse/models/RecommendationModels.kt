package dev.riss.muse.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.math.ln
import kotlin.random.Random

// --- Define Interfaces (Renamed) ---
interface IUserData { // RENAMED
    val username: String?
}

interface IPostData { // RENAMED
    val user: IUserData // Use renamed interface
}

interface IPostWithEngagementData { // RENAMED
    val post: IPostData // Use renamed interface
    val likeCount: Int
    val commentCount: Int
}
// --- End Interfaces ---

@Serializable
data class DiscoveryPost(
    val id: Int,
    @SerialName("image_url") val image_url: String,
    @SerialName("content") val content: String,
    @SerialName("category") val category: String,
    @SerialName("users") override val user: UserInfo, // Implement PostData via UserInfo
    @SerialName("created_at") val created_at: String? = null
) : IPostData // Implement PostData

// Modify UserInfo to implement RENAMED interface
@Serializable
data class UserInfo(
    val id: Int,
    override val username: String, // Implement UserData
    val email: String,
    val profile_image: String? = null,
    val bio: String? = null,
    val auth_id: String
) : IUserData

/**
 * Model to hold all metrics needed for scoring posts
 */
data class PostMetrics(
    val postId: Int,
    val likeCount: Int,
    val commentCount: Int,
    val authorEngagement: Float, // Average engagement per post for the author
    val createdAt: Long // Timestamp for recency calculation if needed
)

/**
 * Helper class for the recommendation algorithm implementation
 */
object RecommendationEngine {
    // Weights for different factors (can be adjusted)
    private const val WEIGHT_LIKES = 1.0f
    private const val WEIGHT_COMMENTS = 1.2f
    private const val WEIGHT_RATIO = 0.8f
    private const val WEIGHT_AUTHOR = 0.6f
    private const val WEIGHT_RANDOM = 0.3f
    private const val MAX_RATIO = 2.0f
    
    /**
     * Main scoring function that combines all factors
     */
    fun score(metrics: PostMetrics): Float {
        // 1. Raw Engagement
        val normalizedLikes = ln(metrics.likeCount + 1f) * WEIGHT_LIKES
        val normalizedComments = ln(metrics.commentCount + 1f) * WEIGHT_COMMENTS
        
        // 2. Quality Signal
        val ratio = if (metrics.likeCount > 0) 
            metrics.commentCount.toFloat() / metrics.likeCount 
        else 
            metrics.commentCount.toFloat()
        val normalizedRatio = (ratio.coerceAtMost(MAX_RATIO) / MAX_RATIO) * WEIGHT_RATIO
        
        // 3. Creator Reputation
        val normalizedAuthorEngagement = ln(metrics.authorEngagement + 1f) * WEIGHT_AUTHOR
        
        // 4. Novelty
        val randomFactor = Random.nextFloat() * WEIGHT_RANDOM
        
        // Combine all factors
        return normalizedLikes + normalizedComments + normalizedRatio + 
               normalizedAuthorEngagement + randomFactor
    }
    
    /**
     * Compute author engagement (GENERIC VERSION)
     * Accepts any list where elements implement RENAMED interface
     */
    fun <T : IPostWithEngagementData> computeAuthorEngagement(posts: List<T>): Map<String, Float> { // Use renamed interface
        val authorPostCounts = mutableMapOf<String, Int>()
        val authorEngagementSums = mutableMapOf<String, Int>()
        
            // Increment post count for this author
        posts.forEach { postData -> 
            val username = postData.post.user.username ?: return@forEach
            authorPostCounts[username] = (authorPostCounts[username] ?: 0) + 1
            // Add engagement metrics
            val engagement = postData.likeCount + postData.commentCount
            authorEngagementSums[username] = (authorEngagementSums[username] ?: 0) + engagement
        }
        
        // Calculate average engagement
        return authorPostCounts.mapValues { (username, postCount) ->
            val totalEngagement = authorEngagementSums[username] ?: 0
            if (postCount > 0) totalEngagement.toFloat() / postCount else 0f
        }
    }
}

/**
 * A model that combines a post with its engagement counts
 * Implement the RENAMED interface
 */
data class DiscoveryPostWithCounts(
    override val post: DiscoveryPost, // DiscoveryPost now implements PostData
    override val likeCount: Int,
    override val commentCount: Int
) : IPostWithEngagementData // Implement the interface

/**
 * A model that combines a post with all metrics and its final score
 */
data class ScoredPost(
    val post: DiscoveryPost,
    val metrics: PostMetrics,
    val score: Float
) 