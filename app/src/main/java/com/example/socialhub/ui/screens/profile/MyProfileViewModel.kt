package com.example.socialhub.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialhub.data.local.entity.UserEntity
import com.example.socialhub.data.local.session.CurrentUserStore
import com.example.socialhub.data.repository.PostRepository
import com.example.socialhub.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import retrofit2.HttpException

/**
 * UI state for the Profile screen.
 *
 * `user == null` means there is no current session.
 * `isLoading` avoids treating the initial DataStore emission as a missing user.
 * `errorMessage` surfaces non-fatal refresh failures to the UI.
 */
data class ProfileUiState(
    val user: UserEntity?,
    val posts: List<com.example.socialhub.data.local.entity.PostEntity>,
    val isLoading: Boolean,
    val errorMessage: String?
)

/**
 * Resolves the current session (DataStore) to a full UserEntity from Room.
 *
 * Data sources:
 * - Session: `CurrentUserStore.currentUserId`.
 * - User record: `UserRepository.observeUser()`.
 * - Profile posts: `PostRepository.observeByUser()` + refresh.
 *
 * UI contract:
 * - Emits a `StateFlow<ProfileUiState>` for Compose.
 * - Surfaces `isLoading`/`errorMessage` during refresh.
 *
 * Internal flow:
 * 1) Session id from DataStore selects the user scope.
 * 2) When present, combine user + posts + flags into UI state.
 * 3) `onStart` triggers a remote refresh while keeping cached data visible.
 */
@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val currentUserStore: CurrentUserStore,
    private val postRepository: PostRepository
) : ViewModel() {
    // Drives the inline loading indicator in the profile UI.
    private val isLoading = MutableStateFlow(false)
    // Holds the most recent refresh error, if any.
    private val errorMessage = MutableStateFlow<String?>(null)

    /**
     * Resolve the current user id to a full user record from Room.
     * `flatMapLatest` re-subscribes to Room when the current user id changes.
     */
    val uiState: StateFlow<ProfileUiState> = currentUserStore.currentUserId
        .flatMapLatest { userId ->
            if (userId == null) {
                // No session: expose null user and stop loading.
                flowOf(
                    ProfileUiState(
                        user = null,
                        posts = emptyList(),
                        isLoading = false,
                        errorMessage = null
                    )
                )
            } else {
                // Session exists: observe the row so UI updates if it changes.
                combine(
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
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            // Start in a loading state until DataStore emits the first value.
            ProfileUiState(
                user = null,
                posts = emptyList(),
                isLoading = true,
                errorMessage = null
            )
        )

    /**
     * Clears the current session without deleting any user rows.
     */
    fun logout() {
        // Clears the current session without touching the database rows.
        viewModelScope.launch {
            currentUserStore.clearCurrentUserId()
        }
    }
}
