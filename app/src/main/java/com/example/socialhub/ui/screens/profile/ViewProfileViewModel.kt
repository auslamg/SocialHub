package com.example.socialhub.ui.screens.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialhub.data.local.dao.UserDao
import com.example.socialhub.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn

/**
 * Loads another user's profile by ID from the navigation route.
 *
 * Responsibilities:
 * - Read the `userId` argument from `SavedStateHandle`.
 * - Expose a `ProfileUiState` stream for the View Profile screen.
 */
@HiltViewModel
class ViewProfileViewModel @Inject constructor(
    private val userDao: UserDao,
    private val postRepository: PostRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    // Extract the ID once; null means the route argument was missing or invalid.
    private val userId: Long? = savedStateHandle.get<Long>("userId")

    /**
     * Exposes the observed user row if the ID is present; otherwise returns null state.
     */
    val uiState: StateFlow<ProfileUiState> = when (userId) {
        // Missing argument: return a stable empty state rather than crashing.
        null -> flowOf(ProfileUiState(user = null, posts = emptyList(), isLoading = false))
        else -> combine(
            userDao.observeUser(userId),
            postRepository.observeByUser(userId)
        ) { user, posts ->
            ProfileUiState(user = user, posts = posts, isLoading = false)
        }
            .onStart {
                // Refresh profile posts before emitting cached data.
                try {
                    postRepository.refreshPostsForUser(userId)
                } catch (_: Exception) {
                    // Network errors are non-fatal; cached posts still display.
                }
            }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ProfileUiState(user = null, posts = emptyList(), isLoading = true)
    )
}
