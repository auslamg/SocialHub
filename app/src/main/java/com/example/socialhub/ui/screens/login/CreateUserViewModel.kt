package com.example.socialhub.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialhub.data.local.entity.UserEntity
import com.example.socialhub.data.local.session.CurrentUserStore
import com.example.socialhub.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

/**
 * UI state for the Create User screen.
 *
 * This is a single immutable object; every change produces a new copy to
 * keep UI updates predictable and traceable.
 */
data class CreateUserUiState(
    val name: String = "",
    val username: String = "",
    val usernameError: String? = null,
    val email: String = "",
    val emailError: String? = null,
    val avatarUrl: String = "",
    val bio: String = "",
    val isSaving: Boolean = false
)

/**
 * Handles user creation and validation.
 *
 * Responsibilities:
 * - Track form input and validation errors.
 * - Persist the new user to Room.
 * - Mark the created user as the current session in DataStore.
 * - Emit a one-shot navigation event on success.
 */
@HiltViewModel
class CreateUserViewModel @Inject constructor(
    private val userRepository: UserRepository,
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
            // Validate as the user types to surface issues early.
            usernameError = validateUsername(value)
        )
    }

    fun onAvatarUrlChange(value: String) {
        uiState = uiState.copy(avatarUrl = value)
    }

    fun onEmailChange(value: String) {
        uiState = uiState.copy(
            email = value,
            // Keep error state in sync with the latest input.
            emailError = validateEmail(value)
        )
    }

    fun onBioChange(value: String) {
        uiState = uiState.copy(bio = value)
    }

    /**
     * Validates form fields and persists a new user.
     *
     * On success, this sets the newly created user as the current session
     * and emits a navigation event for the screen to consume.
     */
    fun registerUser() {
        // Validates and persists a new user, then marks them as current.
        val name = uiState.name.trim()
        val username = uiState.username.trim()
        val email = uiState.email.trim()
        val usernameError = validateUsername(username)
        val emailError = validateEmail(email)
        // Abort early if inputs are missing or invalid.
        if (name.isBlank() || username.isBlank() || usernameError != null || emailError != null) {
            uiState = uiState.copy(
                usernameError = usernameError,
                emailError = emailError
            )
            return
        }

        viewModelScope.launch {
            // Persisting might be slow; the UI disables the button while saving.
            uiState = uiState.copy(isSaving = true)
            if (userRepository.existsUsername(username)) {
                // Keep DB unique constraint behavior consistent with UI validation.
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
                email = email.ifBlank { null },
                avatarUrl = uiState.avatarUrl.trim().ifBlank { null },
                bio = uiState.bio.trim().ifBlank { null },
                followersCount = 0,
                followingCount = 0,
                postsCount = 0
            )
            userRepository.upsertUser(user)
            currentUserStore.setCurrentUserId(user.id)
            uiState = uiState.copy(isSaving = false)
            // Emit navigation event to move to Profile.
            _navigation.tryEmit(Unit)
        }
    }

    /**
     * Username validation: non-empty, alphanumeric + '_' or '-' only.
     */
    private fun validateUsername(value: String): String? {
        val trimmed = value.trim()
        if (trimmed.isBlank()) {
            return null
        }
        val valid = trimmed.matches(Regex("^[A-Za-z0-9_-]+$"))
        return if (valid) null else "Only letters, numbers, _ or -"
    }

    /**
     * Email format: n(xxx.)+x+@+n(xxx.)+xxx
     */
    private fun validateEmail(value: String): String? {
        val trimmed = value.trim()
        if (trimmed.isBlank()) {
            return "Email is required"
        }
        val emailRegex = Regex(
            "^[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)+$"
        )
        return if (emailRegex.matches(trimmed)) null else "Email format is invalid"
    }
}
