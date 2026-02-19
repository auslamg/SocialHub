package com.example.socialhub.ui.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialhub.data.local.dao.UserDao
import com.example.socialhub.data.local.entity.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class ProfileUiState(
    val user: UserEntity?,
    val isLoading: Boolean
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    userDao: UserDao
) : ViewModel() {
    val uiState: StateFlow<ProfileUiState> = userDao.observeUsers()
        .map { users ->
            ProfileUiState(
                user = users.maxByOrNull { it.id },
                isLoading = false
            )
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            ProfileUiState(user = null, isLoading = true)
        )
}
