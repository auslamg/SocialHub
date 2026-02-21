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

data class ProfileUiState(
    val user: UserEntity?,
    val isLoading: Boolean
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userDao: UserDao,
    private val currentUserStore: CurrentUserStore
) : ViewModel() {
    // Resolve the current user id to a full user record from Room.
    val uiState: StateFlow<ProfileUiState> = currentUserStore.currentUserId
        .flatMapLatest { userId ->
            if (userId == null) {
                flowOf(ProfileUiState(user = null, isLoading = false))
            } else {
                userDao.observeUser(userId)
                    .map { user -> ProfileUiState(user = user, isLoading = false) }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            ProfileUiState(user = null, isLoading = true)
        )

    fun logout() {
        // Clears the current session without touching the database rows.
        viewModelScope.launch {
            currentUserStore.clearCurrentUserId()
        }
    }
}
