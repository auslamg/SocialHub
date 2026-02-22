package com.example.socialhub.data.repository

import com.example.socialhub.data.local.dao.PostDao
import com.example.socialhub.data.local.entity.PostEntity
import com.example.socialhub.data.remote.api.PostApi
import com.example.socialhub.data.remote.dto.RemotePostDto
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random
import kotlinx.coroutines.flow.Flow

@Singleton
class PostRepository @Inject constructor(
    private val postDao: PostDao,
    private val postApi: PostApi
) {
    fun observeTimeline(): Flow<List<PostEntity>> = postDao.observeTimeline()

    fun observeByUser(userId: Long): Flow<List<PostEntity>> = postDao.observeByUser(userId)

    suspend fun refreshPosts(limit: Int): List<PostEntity> {
        // Pull the latest feed posts from the API.
        val remotePosts = postApi.getPosts(limit).posts

        // Map API data to Room entities with generated timestamps.
        val entities = remotePosts.map { remote ->
            remote.toEntity()
        }

        // Persist to DB entities.
        postDao.upsertAll(entities)
        return entities
    }

    suspend fun refreshPostsForUser(userId: Long): List<PostEntity> {
        // Pull profile-specific posts from the API.
        val remotePosts = postApi.getPostsByUser(userId).posts

        // Map API data to Room entities with generated timestamps.
        val entities = remotePosts.map { remote ->
            remote.toEntity()
        }

        // Persist so profile screens render from local data.
        postDao.upsertAll(entities)
        return entities
    }

    suspend fun createLocalPost(userId: Long, content: String) {
        val now = System.currentTimeMillis()
        val post = PostEntity(
            id = now,
            userId = userId,
            content = content.trim(),
            createdAt = now,
            updatedAt = null,
            likeCount = 0,
            dislikeCount = 0,
            commentCount = 0,
            isDraft = false
        )
        postDao.upsert(post)
    }

    private fun RemotePostDto.toEntity() = PostEntity(
        id = id,
        userId = userId,
        content = body.trim(),
        // API lacks timestamps, so generate a random time within the last 24h.
        createdAt = System.currentTimeMillis() - Random.nextLong(0L, ONE_DAY_MILLIS),
        updatedAt = null,
        likeCount = reactions.likes,
        dislikeCount = reactions.dislikes,
        commentCount = 0,
        isDraft = false
    )

    private companion object {
        private const val ONE_DAY_MILLIS = 24 * 60 * 60 * 1000L
    }
}
