package com.example.socialhub.data.repository

import com.example.socialhub.data.local.dao.PostDao
import com.example.socialhub.data.local.entity.PostEntity
import com.example.socialhub.data.remote.api.PostApi
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow

@Singleton
class PostRepository @Inject constructor(
    private val postDao: PostDao,
    private val postApi: PostApi
) {
    fun observeTimeline(): Flow<List<PostEntity>> = postDao.observeTimeline()

    fun observeByUser(userId: Long): Flow<List<PostEntity>> = postDao.observeByUser(userId)

    suspend fun refreshPosts(limit: Int): List<PostEntity> {
        val remotePosts = postApi.getPosts(limit)
        val now = System.currentTimeMillis()
        val entities = remotePosts.posts.mapIndexed { index, remote ->
            PostEntity(
                id = remote.id,
                userId = remote.userId,
                content = remote.body.trim(),
                createdAt = now - (index * 60_000L),
                updatedAt = null,
                likeCount = remote.reactions.likes,
                dislikeCount = remote.reactions.dislikes,
                commentCount = 0,
                isDraft = false
            )
        }
        postDao.upsertAll(entities)
        return entities
    }

    suspend fun refreshPostsForUser(userId: Long): List<PostEntity> {
        val remotePosts = postApi.getPostsByUser(userId)
        val now = System.currentTimeMillis()
        val entities = remotePosts.posts.mapIndexed { index, remote ->
            PostEntity(
                id = remote.id,
                userId = remote.userId,
                content = remote.body.trim(),
                createdAt = now - (index * 60_000L),
                updatedAt = null,
                likeCount = remote.reactions.likes,
                dislikeCount = remote.reactions.dislikes,
                commentCount = 0,
                isDraft = false
            )
        }
        postDao.upsertAll(entities)
        return entities
    }
}
