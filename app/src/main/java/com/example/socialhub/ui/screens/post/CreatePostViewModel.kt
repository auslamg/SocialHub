package com.example.socialhub.ui.screens.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialhub.data.local.session.CurrentUserStore
import com.example.socialhub.data.repository.PostRepository
import com.example.socialhub.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Manages Create Post form state, validation, and persistence.
 *
 * Data sources:
 * - Session: `CurrentUserStore.currentUserId` (guest vs signed-in).
 * - User details: `UserRepository.observeUser()` for display metadata.
 * - Posts: `PostRepository.createLocalPost()` for persistence.
 *
 * UI contract:
 * - Exposes a `StateFlow<CreatePostUiState>` with content, validation, and user metadata.
 * - Disables posting for guests and while saving.
 *
 * Internal flow:
 * 1) `contentFlow` tracks the draft and updates on each keystroke.
 * 2) `currentUserId` is derived from DataStore and drives `currentUser`.
 * 3) `combine` merges draft + save state + user into one UI model.
 * 4) `submitPost()` validates and persists, then clears the draft.
 */
@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val currentUserStore: CurrentUserStore,
    private val userRepository: UserRepository
) : ViewModel() {
    // Raw draft content that updates on each keystroke.
    private val contentFlow = MutableStateFlow("")
    // Save state to disable the button and avoid double-submits.
    private val isSaving = MutableStateFlow(false)

    // Observes the current session user id. Null means "guest".
    private val currentUserId = currentUserStore.currentUserId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val currentUser = currentUserId
        .flatMapLatest { userId ->
            if (userId == null) {
                flowOf(null)
            } else {
                userRepository.observeUser(userId)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    /**
     * Aggregated UI state for the Create Post screen.
     *
     * Notes:
     * - `isGuest` disables posting for unauthenticated users.
     * - `canPost` is the single source of truth for button enablement.
     * - User metadata is null when the session is missing or still loading.
     */
    val uiState: StateFlow<CreatePostUiState> = combine(
        contentFlow,
        isSaving,
        currentUser
    ) { content, saving, user ->
        // Validate content on each update so the UI stays in sync.
        val error = validateContent(content)
        val trimmed = content.trim()
        val isGuest = user == null
        CreatePostUiState(
            content = content,
            contentError = error,
            charCount = content.length,
            isSaving = saving,
            isGuest = isGuest,
            userName = user?.name,
            userHandle = user?.username?.let { "@$it" },
            avatarUrl = user?.avatarUrl,
            userBio = user?.bio,
            postsCount = user?.postsCount ?: 0,
            followersCount = user?.followersCount ?: 0,
            followingCount = user?.followingCount ?: 0,
            canPost = !saving && !isGuest && error == null && trimmed.isNotEmpty()
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        CreatePostUiState()
    )

    /**
     * Updates draft content as the user types.
     */
    fun onContentChange(value: String) {
        // Keep the draft in sync with the text field.
        contentFlow.value = value
    }

    /**
     * Validates and persists the post if allowed.
     */
    fun submitPost() {
        // Guard against guest posting or invalid input.
        val userId = currentUserId.value ?: return
        val content = contentFlow.value.trim()
        if (content.isBlank() || validateContent(contentFlow.value) != null) {
            return
        }

        viewModelScope.launch {
            // Prevent duplicate submissions while the insert is running.
            isSaving.value = true
            try {
                postRepository.createLocalPost(userId = userId, content = content)
                // Reset the draft on success.
                contentFlow.value = ""
            } finally {
                isSaving.value = false
            }
        }
    }

    /**
     * Returns a validation error message, or null if valid.
     */
    private fun validateContent(content: String): String? {
        return if (content.length > MAX_POST_LENGTH) {
            "Post must be $MAX_POST_LENGTH characters or less"
        } else {
            null
        }
    }

    private companion object {
        private const val MAX_POST_LENGTH = 280
    }
}

/**
 * Immutable UI state for Create Post.
 */
data class CreatePostUiState(
    val content: String = "",
    val contentError: String? = null,
    val charCount: Int = 0,
    val isSaving: Boolean = false,
    val isGuest: Boolean = true,
    val userName: String? = null,
    val userHandle: String? = null,
    val avatarUrl: String? = null,
    val userBio: String? = null,
    val postsCount: Int = 0,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
    val canPost: Boolean = false
)
