package com.example.socialhub.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialhub.data.local.dao.UserDao
import com.example.socialhub.data.local.entity.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
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
    private val queryFlow = MutableStateFlow("")

    val uiState: StateFlow<SearchUiState> = queryFlow
        .debounce(200)
        .flatMapLatest { query ->
            val trimmed = query.trim()
            if (trimmed.isBlank()) {
                flowOf(SearchUiState(query = query, results = emptyList()))
            } else {
                userDao.searchByUsername(trimmed)
                    .map { results -> SearchUiState(query = query, results = results) }
            }
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5_000),
            SearchUiState()
        )

    fun onQueryChange(value: String) {
        queryFlow.value = value
    }
}

data class SearchUiState(
    val query: String = "",
    val results: List<UserEntity> = emptyList()
)
