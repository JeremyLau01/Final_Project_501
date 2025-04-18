package com.example.creativecommunity.pages

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.creativecommunity.SupabaseClient
import com.example.creativecommunity.models.Post
import com.example.creativecommunity.models.UserData
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Prompt(
    val title: String
)

@Serializable
data class FeedPost(
    val id: Int,
    @SerialName("image_url") val image_url: String,
    @SerialName("content") val content: String,
    @SerialName("users") val user: UserData
)

@Composable
fun CategoryFeed(navController: NavController, category: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "$category Community!",
                modifier = Modifier.padding(top = 30.dp),
            )
            Spacer(modifier = Modifier.height(20.dp))

            var promptTitle by remember { mutableStateOf("Loading prompt...") }

            LaunchedEffect(category) {
                try {
                    val prompt = withContext(Dispatchers.IO) {
                        SupabaseClient.client.postgrest.from("prompts")
                            .select(Columns.raw("title")) {
                                filter {
                                    eq("category", category)
                                    eq("is_active", true)
                                }
                            }
                            .decodeSingle<Prompt>()
                    }
                    promptTitle = "This week's prompt: ${prompt.title}"
                } catch (e: Exception) {
                    promptTitle = "Failed to load prompt: ${e.message}"
                }
            }

            Text(text = promptTitle)

            var posts by remember { mutableStateOf<List<FeedPost>>(emptyList()) }
            var fetchError by remember { mutableStateOf<String?>(null) }

            var showPfpDialog by remember { mutableStateOf(false) }
            var selectedPfpUrl by remember { mutableStateOf<String?>(null) }

            if (showPfpDialog && selectedPfpUrl != null) {
                Dialog(onDismissRequest = { showPfpDialog = false }) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        AsyncImage(
                            model = selectedPfpUrl,
                            contentDescription = "Enlarged profile picture",
                            modifier = Modifier
                                .size(500.dp)
                                .clickable { showPfpDialog = false }
                        )
                    }
                }
            }

            LaunchedEffect(category) {
                try {
                    val fetchedPosts = withContext(Dispatchers.IO) {
                        val result = SupabaseClient.client.postgrest.from("posts")
                            .select(Columns.raw("id, image_url, content, user_id, users!inner(profile_image, username)")) {
                                filter {
                                    eq("category", category)
                                }
                                order("created_at", io.github.jan.supabase.postgrest.query.Order.DESCENDING)
                                limit(10)
                            }
                        val postsList = result.decodeList<FeedPost>()
                        Log.d("SupabaseTest", "Fetched posts: $postsList")
                        postsList
                    }
                    posts = fetchedPosts
                } catch (e: Exception) {
                    fetchError = "Failed to load posts: ${e.message}"
                }
            }

            val defaultProfileImages = listOf(
                "https://i.imgur.com/DyFZblf.jpeg", // Gray square
                "https://i.imgur.com/kcbZfpx.png", // Smiley face
                "https://i.imgur.com/WvDsY4x.jpeg", // Simple avatar silhouette
                "https://i.imgur.com/iCy2JU1.jpeg", // Minimalist user icon
                "https://i.imgur.com/7hVHf5f.png"  // Abstract shape
            )

            if (fetchError != null) {
                Text(text = fetchError!!)
            } else if (posts.isEmpty()) {
                Text(text = "No posts yet for this category.")
            } else {
                LazyColumn {
                    items(posts) { post ->
                        val defaultPfp = remember { post.user.profile_image ?: defaultProfileImages.random() }
                        Post(
                            postId = post.id,
                            profileImage = defaultPfp,
                            username = post.user.username ?: "Unknown User",
                            postImage = post.image_url,
                            caption = post.content,
                            likeCount = 0,
                            commentCount = 0,
                            onCommentClicked = {
                                navController.navigate("individual_post/${post.id}")
                            },
                            onProfileClick = {
                                selectedPfpUrl = defaultPfp
                                showPfpDialog = true
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text("All Communities")
            }
            Button(
                onClick = {
                    navController.navigate("new_post/${category}")
                },
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text("+")
            }
        }
    }
}