package com.example.creativecommunity.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun IndividualPostPage(navController: NavController) {
    Column {
        Button(onClick = { navController.popBackStack() }) {
            Text("Back")
        }
        Post(
            profileImage = "https://i.imgur.com/qBPkCnP.jpeg",
            username = "Bob Ross",
            postImage = "https://i.imgur.com/NxjUBgB.jpeg",
            caption = "A Bob Ross Classic",
            likeCount = 15,
            commentCount = 8
        )

        // Same comment
        // run a loop to go through and create comments linked to each post
        Comment(
            profileImage = "https://i.imgur.com/OnlfHHd.jpeg",
            username = "huge_art_fan",
            commentText = "I love this painting - it's beautiful."
        )
    }
}