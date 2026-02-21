package com.example.socialhub.ui.screens.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialhub.data.local.dao.UserDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class ViewProfileViewModel @Inject constructor(
    private val userDao: UserDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val userId: Long? = savedStateHandle.get<Long>("userId")

    val uiState: StateFlow<ProfileUiState> = when (userId) {
        null -> flowOf(ProfileUiState(user = null, isLoading = false))
        else -> userDao.observeUser(userId)
            .map { user -> ProfileUiState(user = user, isLoading = false) }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ProfileUiState(user = null, isLoading = true)
    )
}
