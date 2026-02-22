package com.example.socialhub.ui.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialhub.data.local.entity.UserEntity
import com.example.socialhub.data.remote.api.UserApi
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

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val userApi: UserApi
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
                flow {
                    emit(fetchRemoteUsers(trimmed))
                }
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

    private suspend fun fetchRemoteUsers(query: String): List<UserEntity> {
        return try {
            val remoteUsers = userApi.searchUsers(query)
            remoteUsers.users.map { remote ->
                val bio = buildBio(
                    city = remote.address.city,
                    country = remote.address.country,
                    university = remote.university,
                    companyName = remote.company.name
                )
                UserEntity(
                    id = remote.id,
                    username = remote.username,
                    name = "${remote.firstName} ${remote.lastName}".trim(),
                    email = remote.email,
                    avatarUrl = "https://i.pravatar.cc/150?u=${remote.username}",
                    bio = bio,
                    followersCount = 0,
                    followingCount = 0,
                    postsCount = 0
                )
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun buildBio(
        city: String,
        country: String,
        university: String,
        companyName: String
    ): String {
        val location = listOf(city.trim(), country.trim())
            .filter { it.isNotBlank() }
            .joinToString(", ")
        val lineOne = if (location.isNotBlank() && university.isNotBlank()) {
            "$location - ${university.trim()}"
        } else {
            listOf(location, university.trim()).filter { it.isNotBlank() }.joinToString(" ")
        }
        return listOf(lineOne, companyName.trim())
            .filter { it.isNotBlank() }
            .joinToString("\n")
    }
}

// Search screen UI state: current query + matching users.
data class SearchUiState(
    val query: String = "",
    val results: List<UserEntity> = emptyList()
)
