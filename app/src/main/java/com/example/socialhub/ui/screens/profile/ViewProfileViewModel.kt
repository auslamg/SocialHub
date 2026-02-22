package com.example.socialhub.ui.screens.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialhub.data.repository.PostRepository
import com.example.socialhub.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import retrofit2.HttpException

/**
 * Loads another user's profile by ID from the navigation route.
 *
 * Responsibilities:
 * - Read the `userId` argument from `SavedStateHandle`.
 * - Expose a `ProfileUiState` stream for the View Profile screen.
 */
@HiltViewModel
class ViewProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val postRepository: PostRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val isLoading = MutableStateFlow(false)
    private val errorMessage = MutableStateFlow<String?>(null)

    // Extract the ID once; null means the route argument was missing or invalid.
    private val userId: Long? = savedStateHandle.get<Long>("userId")

    /**
     * Exposes the observed user row if the ID is present; otherwise returns null state.
     */
    val uiState: StateFlow<ProfileUiState> = when (userId) {
        // Missing argument: return a stable empty state rather than crashing.
        null -> flowOf(
            ProfileUiState(
                user = null,
                posts = emptyList(),
                isLoading = false,
                errorMessage = null
            )
        )
        else -> combine(
            userRepository.observeUser(userId),
            postRepository.observeByUser(userId),
            isLoading,
            errorMessage
        ) { user, posts, loading, error ->
            ProfileUiState(
                user = user,
                posts = posts,
                isLoading = loading,
                errorMessage = error
            )
        }
            .onStart {
                // Refresh profile posts before emitting cached data.
                isLoading.value = true
                errorMessage.value = null
                try {
                    postRepository.refreshPostsForUser(userId)
                } catch (error: Exception) {
                    // Network errors are non-fatal; cached posts still display.
                    val isLocalOnly = when (error) {
                        is HttpException -> error.code() == 404 &&
                            userRepository.getUser(userId) != null
                        else -> false
                    }
                    if (!isLocalOnly) {
                        errorMessage.value = error.message
                            ?: "Couldn't load profile updates."
                    }
                } finally {
                    isLoading.value = false
                }
            }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ProfileUiState(
            user = null,
            posts = emptyList(),
            isLoading = true,
            errorMessage = null
        )
    )
}
