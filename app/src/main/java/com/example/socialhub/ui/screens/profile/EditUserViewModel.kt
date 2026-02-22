package com.example.socialhub.ui.screens.profile

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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

/**
 * Drives the Edit Profile screen state and persistence.
 *
 * Flow summary:
 * - Pull the current user id from `CurrentUserStore` on init.
 * - Load the user from `UserRepository` and hydrate the form fields.
 * - Track mutable form state via Compose `mutableStateOf` for instant UI updates.
 * - Save/delete operations run in `viewModelScope` to avoid blocking the UI.
 * - Emits one-shot navigation events through `SharedFlow`.
 */
@HiltViewModel
class EditUserViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val currentUserStore: CurrentUserStore
) : ViewModel() {
    // One-time navigation events; not replayed on configuration changes.
    private val _navigation = MutableSharedFlow<EditUserNavigation>(extraBufferCapacity = 1)
    val navigation = _navigation.asSharedFlow()

    // Backing state for the form fields rendered by Compose.
    var uiState by mutableStateOf(EditUserUiState())
        private set

    // Cached user record so updates/deletes use the original id.
    private var currentUser: UserEntity? = null

    init {
        viewModelScope.launch {
            // Resolve the current session to a user record, if any.
            val userId = currentUserStore.currentUserId.first()
            if (userId != null) {
                val user = userRepository.getUser(userId)
                if (user != null) {
                    currentUser = user
                    // Seed the form fields with persisted values.
                    uiState = uiState.copy(
                        name = user.name,
                        username = user.username,
                        email = user.email.orEmpty(),
                        avatarUrl = user.avatarUrl.orEmpty(),
                        bio = user.bio.orEmpty()
                    )
                }
            }
        }
    }

    fun onNameChange(value: String) {
        uiState = uiState.copy(name = value)
    }

    fun onEmailChange(value: String) {
        uiState = uiState.copy(
            email = value,
            emailError = validateEmail(value)
        )
    }

    fun onAvatarUrlChange(value: String) {
        uiState = uiState.copy(avatarUrl = value)
    }

    fun onBioChange(value: String) {
        uiState = uiState.copy(bio = value)
    }

    fun saveChanges() {
        val user = currentUser ?: return
        val name = uiState.name.trim()
        val email = uiState.email.trim()
        val emailError = validateEmail(email)
        if (name.isBlank() || emailError != null) {
            // Reject invalid input and surface the validation message.
            uiState = uiState.copy(emailError = emailError)
            return
        }

        viewModelScope.launch {
            // Persist updates while the UI shows a saving state.
            uiState = uiState.copy(isSaving = true)
            val updated = user.copy(
                name = name,
                email = email.ifBlank { null },
                avatarUrl = uiState.avatarUrl.trim().ifBlank { null },
                bio = uiState.bio.trim().ifBlank { null }
            )
            userRepository.updateUser(updated)
            uiState = uiState.copy(isSaving = false)
            // Signal navigation back to the profile screen.
            _navigation.tryEmit(EditUserNavigation.BackToProfile)
        }
    }

    fun deleteAccount() {
        val user = currentUser ?: return
        viewModelScope.launch {
            // Deleting is destructive; keep the UI disabled until complete.
            uiState = uiState.copy(isDeleting = true)
            userRepository.deleteUser(user)
            currentUserStore.clearCurrentUserId()
            uiState = uiState.copy(isDeleting = false)
            // Navigate away once the account is removed.
            _navigation.tryEmit(EditUserNavigation.BackToProfile)
        }
    }

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

/**
 * UI state for the Edit Profile form.
 */
data class EditUserUiState(
    val name: String = "",
    val username: String = "",
    val email: String = "",
    val emailError: String? = null,
    val avatarUrl: String = "",
    val bio: String = "",
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false
)

/**
 * One-shot navigation events emitted by the Edit Profile screen.
 */
enum class EditUserNavigation {
    BackToProfile
}
