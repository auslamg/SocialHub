package com.example.socialhub.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialhub.data.local.session.Auth0SessionStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/**
 * Exposes Auth0 session state as UI-ready status text.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    auth0SessionStore: Auth0SessionStore
) : ViewModel() {
    /**
     * Reactive auth status used by the Auth screen.
     */
    val uiState: StateFlow<AuthUiState> = auth0SessionStore.authSession
        .map { session ->
            if (!session.isLoggedIn) {
                AuthUiState(
                    isLoggedIn = false,
                    statusText = "Status: Not signed in"
                )
            } else {
                val label = when {
                    !session.name.isNullOrBlank() && !session.email.isNullOrBlank() ->
                        "Status: Signed in as ${session.name} (${session.email})"
                    !session.name.isNullOrBlank() -> "Status: Signed in as ${session.name}"
                    !session.email.isNullOrBlank() -> "Status: Signed in as ${session.email}"
                    else -> "Status: Signed in"
                }
                AuthUiState(isLoggedIn = true, statusText = label)
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            AuthUiState(isLoggedIn = false, statusText = "Status: Not signed in")
        )
}

/**
 * UI state for auth status and label text.
 */
data class AuthUiState(
    val isLoggedIn: Boolean,
    val statusText: String
)
