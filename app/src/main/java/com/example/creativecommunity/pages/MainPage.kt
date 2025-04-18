package com.example.creativecommunity.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MainPage(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Select a Category",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(modifier = Modifier.height(16.dp))

        val categoryDisplayMap = mapOf(
            "ART" to "Visual Arts",
            "CODING" to "Programming",
            "ENGINEERING" to "Engineering Projects",
            "PHOTO" to "Photography",
            "WRITING" to "Creative Writing",
            "MUSIC" to "Music Creation",
            "CRAFTS" to "Handmade Crafts",
            "COOKING" to "Culinary Arts",
            "FILM" to "Filmmaking",
            "SCIENCE" to "Science Experiments"
        )

        categoryDisplayMap.forEach { (key, displayName) ->
            Button(
                onClick = {
                    navController.navigate("category_feed/${key}")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Text(displayName)
            }
        }
    }
}