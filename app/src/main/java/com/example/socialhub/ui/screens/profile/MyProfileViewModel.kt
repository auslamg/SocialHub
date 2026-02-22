package com.example.socialhub.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialhub.data.local.dao.UserDao
import com.example.socialhub.data.local.entity.UserEntity
import com.example.socialhub.data.local.session.CurrentUserStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * UI state for the Profile screen.
 *
 * `user == null` means there is no current session.
 * `isLoading` avoids treating the initial DataStore emission as a missing user.
 */
data class ProfileUiState(
    val user: UserEntity?,
    val isLoading: Boolean
)

/**
 * Resolves the current session (DataStore) to a full UserEntity from Room.
 *
 * Responsibilities:
 * - Observe the current user ID from DataStore.
 * - Translate the ID into a Room stream for live updates.
 * - Provide a small `ProfileUiState` for Compose.
 */
@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val userDao: UserDao,
    private val currentUserStore: CurrentUserStore
) : ViewModel() {
    /**
     * Resolve the current user id to a full user record from Room.
     * `flatMapLatest` re-subscribes to Room when the current user id changes.
     */
    val uiState: StateFlow<ProfileUiState> = currentUserStore.currentUserId
        .flatMapLatest { userId ->
            if (userId == null) {
                // No session: expose null user and stop loading.
                flowOf(ProfileUiState(user = null, isLoading = false))
            } else {
                // Session exists: observe the row so UI updates if it changes.
                userDao.observeUser(userId)
                    .map { user -> ProfileUiState(user = user, isLoading = false) }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            // Start in a loading state until DataStore emits the first value.
            ProfileUiState(user = null, isLoading = true)
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
