package dev.riss.muse.models

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.fillMaxWidth
import kotlinx.serialization.Serializable
import androidx.compose.ui.graphics.Color

@Composable
fun Comment(
    profileImage: String,
    username: String,
    commentText: String,
    onReplyClicked: (() -> Unit)? = null,
    indentLevel: Int = 0
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp + (indentLevel * 24).dp, end = 10.dp, top = 10.dp, bottom = 10.dp)
    ) {
        AsyncImage(
            model = profileImage,
            contentDescription = "Profile picture",
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
        )

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(text = username, color = Color.White)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = commentText, color = Color.White)
            if (onReplyClicked != null) {
                androidx.compose.material3.TextButton(onClick = onReplyClicked) {
                    Text("Reply")
                }
            }
        }
    }
}

@Serializable
data class CommentIdOnly(val id: Int)