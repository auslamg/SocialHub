package com.example.socialhub.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialhub.data.local.dao.UserDao
import com.example.socialhub.data.local.entity.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val userDao: UserDao
) : ViewModel() {
    // Raw user input; kept immediate for responsive text field updates.
    //Kept separate from results to avoid delayed typing.
    private val queryFlow = MutableStateFlow("")

    // Debounced results to avoid querying on every keystroke.
    // Debounce waits for the user to stop typing before running the DAO query.
    private val resultsFlow = queryFlow
        .debounce(1200)
        .flatMapLatest { query ->
            val trimmed = query.trim()
            if (trimmed.isBlank()) {
                // Empty query -> no results and no DB work.
                flowOf(emptyList())
            } else {
                // Prefix search in Room (case-sensitive based on collation).
                userDao.searchByUsername(trimmed)
            }
        }

    // Combined UI state exposed to the screen.
    val uiState: StateFlow<SearchUiState> = combine(queryFlow, resultsFlow) { query, results ->
        SearchUiState(query = query, results = results)
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SearchUiState()
        )

    // Called by the TextField on every keystroke.
    fun onQueryChange(value: String) {
        queryFlow.value = value
    }
}

// Search screen UI state: current query + matching users.
data class SearchUiState(
    val query: String = "",
    val results: List<UserEntity> = emptyList()
)
