package com.example.socialhub.ui.screens.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialhub.data.local.dao.UserDao
import com.example.socialhub.data.local.entity.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class CreateUserUiState(
    val name: String = "",
    val username: String = "",
    val avatarUrl: String = "",
    val bio: String = "",
    val isSaving: Boolean = false
)

@HiltViewModel
class CreateUserViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {
    private val _navigation = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val navigation = _navigation.asSharedFlow()

    var uiState by mutableStateOf(CreateUserUiState())
        private set

    fun onNameChange(value: String) {
        uiState = uiState.copy(name = value)
    }

    fun onUsernameChange(value: String) {
        uiState = uiState.copy(username = value)
    }

    fun onAvatarUrlChange(value: String) {
        uiState = uiState.copy(avatarUrl = value)
    }

    fun onBioChange(value: String) {
        uiState = uiState.copy(bio = value)
    }

    fun registerUser() {
        val name = uiState.name.trim()
        val username = uiState.username.trim()
        if (name.isBlank() || username.isBlank()) {
            return
        }

        viewModelScope.launch {
            uiState = uiState.copy(isSaving = true)
            val user = UserEntity(
                id = System.currentTimeMillis(),
                username = username,
                name = name,
                avatarUrl = uiState.avatarUrl.trim().ifBlank { null },
                bio = uiState.bio.trim().ifBlank { null },
                followersCount = 0,
                followingCount = 0,
                postsCount = 0
            )
            userDao.upsert(user)
            uiState = uiState.copy(isSaving = false)
            _navigation.tryEmit(Unit)
        }
    }
}
