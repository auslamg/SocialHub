package com.example.socialhub.ui.screens.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.socialhub.ui.SocialHubScreenPadding
import com.example.socialhub.ui.components.AnimatedGradientBackground
import com.example.socialhub.ui.components.AppColors
import com.example.socialhub.ui.components.DarkOutlinedTextField

@Composable
fun SearchScreen(
    viewModel: SearchViewModel = hiltViewModel()
) {
    // Search UI that queries the local user table by username prefix.
    val uiState by viewModel.uiState.collectAsState()

    AnimatedGradientBackground {
        Column(modifier = Modifier.padding(SocialHubScreenPadding())) {
            Text(
                text = "Search",
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Bold,
                fontSize = 26.sp,
                color = AppColors.WhiteText
            )
            Text(
                text = "Find people by username.",
                fontFamily = FontFamily.Monospace,
                fontSize = 12.sp,
                color = AppColors.ViridianText
            )
            Spacer(modifier = Modifier.height(16.dp))
            DarkOutlinedTextField(
                value = uiState.query,
                onValueChange = viewModel::onQueryChange,
                label = "Search usernames",
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            if (uiState.query.isBlank()) {
                Text(
                    text = "Start typing to see results.",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = AppColors.ViridianText
                )
            } else if (uiState.results.isEmpty()) {
                Text(
                    text = "No matches found.",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = AppColors.ViridianText
                )
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(uiState.results, key = { it.id }) { user ->
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                text = user.name,
                                fontFamily = FontFamily.Serif,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 16.sp,
                                color = AppColors.WhiteText
                            )
                            Text(
                                text = "@${user.username}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = AppColors.ViridianText
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(100.dp))
        }
    }
}
