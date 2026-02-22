package com.example.socialhub.ui.screens.profile

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
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@HiltViewModel
class EditUserViewModel @Inject constructor(
    private val userDao: UserDao,
    private val currentUserStore: CurrentUserStore
) : ViewModel() {
    private val _navigation = MutableSharedFlow<EditUserNavigation>(extraBufferCapacity = 1)
    val navigation = _navigation.asSharedFlow()

    var uiState by mutableStateOf(EditUserUiState())
        private set

    private var currentUser: UserEntity? = null

    init {
        viewModelScope.launch {
            val userId = currentUserStore.currentUserId.first()
            if (userId != null) {
                val user = userDao.observeUser(userId).first()
                if (user != null) {
                    currentUser = user
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
            uiState = uiState.copy(emailError = emailError)
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true)
            val updated = user.copy(
                name = name,
                email = email.ifBlank { null },
                avatarUrl = uiState.avatarUrl.trim().ifBlank { null },
                bio = uiState.bio.trim().ifBlank { null }
            )
            userDao.update(updated)
            uiState = uiState.copy(isSaving = false)
            _navigation.tryEmit(EditUserNavigation.BackToProfile)
        }
    }

    fun deleteAccount() {
        val user = currentUser ?: return
        viewModelScope.launch {
            uiState = uiState.copy(isDeleting = true)
            userDao.delete(user)
            currentUserStore.clearCurrentUserId()
            uiState = uiState.copy(isDeleting = false)
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

enum class EditUserNavigation {
    BackToProfile
}
