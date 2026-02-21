package com.example.socialhub

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.Modifier
import com.example.socialhub.ui.SocialHubApp
import dagger.hilt.android.AndroidEntryPoint

// Main Android entry point. Hilt injects dependencies for composables via ViewModels.
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Opt into edge-to-edge so content can draw behind system bars.
        enableEdgeToEdge()
        setContent {
            // Root Compose tree for the whole app.
            SocialHubApp()
        }
    }
}