package com.example.socialhub.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialhub.data.local.entity.UserEntity
import com.example.socialhub.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flow

/**
 * Handles search query state and API-driven user lookup.
 *
 * Data sources:
 * - Query input: `queryFlow` (mutable input state).
 * - Results: `UserRepository.searchUsers()` (local cache + remote API).
 *
 * UI contract:
 * - Emits a `StateFlow<SearchUiState>` with query, results, loading, and error.
 * - Keeps input responsive while debouncing remote searches.
 *
 * Internal flow:
 * 1) User types -> `queryFlow` updates immediately.
 * 2) Debounce waits for typing to pause before hitting the repository.
 * 3) Repository returns results + optional error message.
 * 4) Combine input/results/loading/error into a single UI model.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    // Raw user input; kept immediate for responsive text field updates.
    // Kept separate from results to avoid delayed typing.
    private val queryFlow = MutableStateFlow("")

    // Toggles the inline loading indicator in the UI.
    private val isLoading = MutableStateFlow(false)
    // Holds the latest error for the current query.
    private val errorMessage = MutableStateFlow<String?>(null)

    // Debounced results to avoid querying on every keystroke.
    // Debounce waits for the user to stop typing before running the network call.
    private val resultsFlow = queryFlow
        .debounce(1200)
        .flatMapLatest { query ->
            val trimmed = query.trim()
            if (trimmed.isBlank()) {
                // Empty query -> no results and no DB work.
                isLoading.value = false
                errorMessage.value = null
                flowOf(emptyList())
            } else {
                // Cancel any in-flight search when a new query arrives.
                flow {
                    isLoading.value = true
                    errorMessage.value = null
                    try {
                        // Repository returns users + optional error.
                        val result = fetchRemoteUsers(trimmed)
                        errorMessage.value = result.errorMessage
                        emit(result.users)
                    } finally {
                        isLoading.value = false
                    }
                }
            }
        }

    /**
     * Combined UI state exposed to the screen.
     *
     * `stateIn` keeps the flow hot so Compose always has a current value,
     * even after configuration changes.
     */
    val uiState: StateFlow<SearchUiState> = combine(
        queryFlow,
        resultsFlow,
        isLoading,
        errorMessage
    ) { query, results, loading, error ->
        SearchUiState(query = query, results = results, isLoading = loading, errorMessage = error)
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SearchUiState()
        )

    /**
     * Called by the TextField on every keystroke.
     * The debounce logic lives upstream in `resultsFlow`.
     */
    fun onQueryChange(value: String) {
        // Keep input responsive and let the debounce handle throttling.
        queryFlow.value = value
    }

    /**
     * Runs a remote search and maps results to local UI entities.
     * Errors are swallowed so the UI can keep operating gracefully.
     */
    private suspend fun fetchRemoteUsers(query: String): com.example.socialhub.data.repository.SearchUsersResult {
        // Delegate mapping and error handling to the repository layer.
        return userRepository.searchUsers(query)
    }
}

/**
 * Search screen UI state: current query + matching users.
 */
data class SearchUiState(
    val query: String = "",
    val results: List<UserEntity> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
