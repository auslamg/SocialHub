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
 * Responsibilities:
 * - Track raw query input for the TextField.
 * - Debounce input to reduce network load.
 * - Execute remote search and expose results as UI state.
 */
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    // Raw user input; kept immediate for responsive text field updates.
    // Kept separate from results to avoid delayed typing.
    private val queryFlow = MutableStateFlow("")

    // Debounced results to avoid querying on every keystroke.
    // Debounce waits for the user to stop typing before running the network call.
    private val resultsFlow = queryFlow
        .debounce(1200)
        .flatMapLatest { query ->
            val trimmed = query.trim()
            if (trimmed.isBlank()) {
                // Empty query -> no results and no DB work.
                flowOf(emptyList())
            } else {
                // Cancel any in-flight search when a new query arrives.
                flow {
                    emit(fetchRemoteUsers(trimmed))
                }
            }
        }

    /**
     * Combined UI state exposed to the screen.
     *
     * The flow is kept hot with `stateIn` so Compose always has a current value.
     */
    val uiState: StateFlow<SearchUiState> = combine(queryFlow, resultsFlow) { query, results ->
        SearchUiState(query = query, results = results)
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
    private suspend fun fetchRemoteUsers(query: String): List<UserEntity> {
        // Delegate mapping and error handling to the repository layer.
        return userRepository.searchUsers(query)
    }
}

/**
 * Search screen UI state: current query + matching users.
 */
data class SearchUiState(
    val query: String = "",
    val results: List<UserEntity> = emptyList()
)
