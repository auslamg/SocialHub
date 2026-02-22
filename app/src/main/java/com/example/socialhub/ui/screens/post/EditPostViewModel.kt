package com.example.socialhub.ui.screens.post

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.socialhub.data.local.entity.PostEntity
import com.example.socialhub.data.local.dao.UserDao
import com.example.socialhub.data.local.session.CurrentUserStore
import com.example.socialhub.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * Manages Edit Post form state, validation, and persistence.
 */
@HiltViewModel
class EditPostViewModel @Inject constructor(
    private val postRepository: PostRepository,
    private val currentUserStore: CurrentUserStore,
    private val userDao: UserDao,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val postId: Long? = savedStateHandle.get<Long>("postId")

    private val contentFlow = MutableStateFlow("")
    private val isSaving = MutableStateFlow(false)
    private val isDeleting = MutableStateFlow(false)

    private val _navigation = MutableSharedFlow<EditPostNavigation>(extraBufferCapacity = 1)
    val navigation = _navigation.asSharedFlow()

    private var currentPost: PostEntity? = null
    private var hasInitialized = false

    private val currentUserId = currentUserStore.currentUserId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val currentUser = currentUserId
        .flatMapLatest { userId ->
            if (userId == null) {
                flowOf(null)
            } else {
                userDao.observeUser(userId)
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    private val postFlow = when (postId) {
        null -> flowOf(null)
        else -> postRepository.observePost(postId)
    }

    val uiState: StateFlow<EditPostUiState> = combine(
        contentFlow,
        isSaving,
        isDeleting,
        currentUser,
        postFlow
    ) { content, saving, deleting, user, post ->
        val error = validateContent(content)
        val trimmed = content.trim()
        val isGuest = user == null
        val postExists = post != null
        val isOwner = post != null && user != null && post.userId == user.id
        EditPostUiState(
            content = content,
            contentError = error,
            charCount = content.length,
            isSaving = saving,
            isDeleting = deleting,
            isGuest = isGuest,
            isOwner = isOwner,
            postExists = postExists,
            userName = user?.name,
            userHandle = user?.username?.let { "@$it" },
            avatarUrl = user?.avatarUrl,
            canSave = !saving && !deleting && !isGuest && isOwner && error == null && trimmed.isNotEmpty(),
            canDelete = !saving && !deleting && !isGuest && isOwner && postExists
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        EditPostUiState()
    )

    init {
        viewModelScope.launch {
            postFlow.collect { post ->
                currentPost = post
                if (post != null && !hasInitialized) {
                    contentFlow.value = post.content
                    hasInitialized = true
                }
            }
        }
    }

    fun onContentChange(value: String) {
        contentFlow.value = value
    }

    fun saveChanges() {
        val post = currentPost ?: return
        val userId = currentUserId.value ?: return
        if (post.userId != userId) {
            return
        }
        val content = contentFlow.value.trim()
        if (content.isBlank() || validateContent(contentFlow.value) != null) {
            return
        }

        viewModelScope.launch {
            isSaving.value = true
            try {
                postRepository.updatePost(
                    post.copy(
                        content = content,
                        updatedAt = System.currentTimeMillis()
                    )
                )
                _navigation.tryEmit(EditPostNavigation.Back)
            } finally {
                isSaving.value = false
            }
        }
    }

    fun deletePost() {
        val post = currentPost ?: return
        val userId = currentUserId.value ?: return
        if (post.userId != userId) {
            return
        }

        viewModelScope.launch {
            isDeleting.value = true
            try {
                postRepository.deletePost(post.id)
                _navigation.tryEmit(EditPostNavigation.Back)
            } finally {
                isDeleting.value = false
            }
        }
    }

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

data class EditPostUiState(
    val content: String = "",
    val contentError: String? = null,
    val charCount: Int = 0,
    val isSaving: Boolean = false,
    val isDeleting: Boolean = false,
    val isGuest: Boolean = true,
    val isOwner: Boolean = false,
    val postExists: Boolean = true,
    val userName: String? = null,
    val userHandle: String? = null,
    val avatarUrl: String? = null,
    val canSave: Boolean = false,
    val canDelete: Boolean = false
)

enum class EditPostNavigation {
    Back
}
