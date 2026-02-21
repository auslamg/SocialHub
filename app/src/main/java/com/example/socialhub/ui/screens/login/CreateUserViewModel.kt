package com.example.socialhub.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialhub.data.local.dao.UserDao
import com.example.socialhub.data.local.entity.UserEntity
import com.example.socialhub.data.local.session.CurrentUserStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

// UI state for the Create User screen.
// Single immutable state object and replace it on changes.
data class CreateUserUiState(
    val name: String = "",
    val username: String = "",
    val usernameError: String? = null,
    val email: String = "",
    val avatarUrl: String = "",
    val bio: String = "",
    val isSaving: Boolean = false
)

// Handles user creation and validation. Persists the user in Room and
// marks them as the current user in the front-end session store (DataStore).
@HiltViewModel
class CreateUserViewModel @Inject constructor(
    private val userDao: UserDao,
    private val currentUserStore: CurrentUserStore
) : ViewModel() {
    // One-shot navigation events for the UI.
    // SharedFlow is used because navigation should not be replayed on rotation.
    private val _navigation = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigation = _navigation.asSharedFlow()

    // Compose observes this mutable state and re-renders on changes.
    var uiState by mutableStateOf(CreateUserUiState())
        private set

    // Field updates are simple copies to keep state immutable.
    fun onNameChange(value: String) {
        uiState = uiState.copy(name = value)
    }

    fun onUsernameChange(value: String) {
        uiState = uiState.copy(
            username = value,
            usernameError = validateUsername(value)
        )
    }

    fun onAvatarUrlChange(value: String) {
        uiState = uiState.copy(avatarUrl = value)
    }

    fun onEmailChange(value: String) {
        uiState = uiState.copy(email = value)
    }

    fun onBioChange(value: String) {
        uiState = uiState.copy(bio = value)
    }

    fun registerUser() {
        // Validates and persists a new user, then marks them as current.
        val name = uiState.name.trim()
        val username = uiState.username.trim()
        val usernameError = validateUsername(username)
        if (name.isBlank() || username.isBlank() || usernameError != null) {
            uiState = uiState.copy(usernameError = usernameError)
            return
        }

        viewModelScope.launch {
            // Persisting might be slow; the UI disables the button while saving.
            uiState = uiState.copy(isSaving = true)
            if (userDao.existsUsername(username)) {
                uiState = uiState.copy(
                    isSaving = false,
                    usernameError = "Username already taken"
                )
                return@launch
            }
            // Current user is a front-end session concept, not a DB column.
            currentUserStore.clearCurrentUserId()
            val user = UserEntity(
                id = System.currentTimeMillis(),
                username = username,
                name = name,
                email = uiState.email.trim().ifBlank { null },
                avatarUrl = uiState.avatarUrl.trim().ifBlank { null },
                bio = uiState.bio.trim().ifBlank { null },
                followersCount = 0,
                followingCount = 0,
                postsCount = 0
            )
            userDao.upsert(user)
            currentUserStore.setCurrentUserId(user.id)
            uiState = uiState.copy(isSaving = false)
            // Emit navigation event to move to Profile.
            _navigation.tryEmit(Unit)
        }
    }

    // Username validation: non-empty, alphanumeric + '_' or '-' only.
    private fun validateUsername(value: String): String? {
        val trimmed = value.trim()
        if (trimmed.isBlank()) {
            return null
        }
        val valid = trimmed.matches(Regex("^[A-Za-z0-9_-]+$"))
        return if (valid) null else "Only letters, numbers, _ or -"
    }
}
