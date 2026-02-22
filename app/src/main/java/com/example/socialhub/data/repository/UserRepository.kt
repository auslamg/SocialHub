package com.example.socialhub.data.repository

import com.example.socialhub.data.local.dao.UserDao
import com.example.socialhub.data.local.entity.UserEntity
import com.example.socialhub.data.remote.api.UserApi
import com.example.socialhub.data.remote.dto.RemoteUserDto
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * Repository for user data, backed by Room and a remote API.
 */
@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val userApi: UserApi
) {
    private val fetchedUserIds = mutableSetOf<Long>()

    /**
     * Stream of all cached users.
     */
    fun observeUsers(): Flow<List<UserEntity>> = userDao.observeUsers()

    /**
     * Stream of a single user by id.
     */
    fun observeUser(userId: Long): Flow<UserEntity?> = userDao.observeUser(userId)

    /**
     * Fetches a user snapshot from the local cache.
     */
    suspend fun getUser(userId: Long): UserEntity? = userDao.observeUser(userId).first()

    /**
     * Checks if a username is already taken locally.
     */
    suspend fun existsUsername(username: String): Boolean = userDao.existsUsername(username)

    /**
     * Inserts or replaces a user record.
     */
    suspend fun upsertUser(user: UserEntity) {
        userDao.upsert(user)
    }

    /**
     * Updates an existing user record.
     */
    suspend fun updateUser(user: UserEntity) {
        userDao.update(user)
    }

    /**
     * Deletes a user record.
     */
    suspend fun deleteUser(user: UserEntity) {
        userDao.delete(user)
    }

    /**
     * Fetches and caches remote users by id, skipping those already fetched.
     */
    suspend fun fetchUsersByIds(userIds: List<Long>) {
        for (userId in userIds) {
            if (!fetchedUserIds.add(userId)) {
                continue
            }
            try {
                val remote = userApi.getUser(userId)
                userDao.upsert(remote.toEntity())
            } catch (_: Exception) {
                // Non-fatal; keep placeholders if a user lookup fails.
            }
        }
    }

    /**
     * Searches users locally and remotely, returning merged results.
     */
    suspend fun searchUsers(query: String): SearchUsersResult {
        val trimmed = query.trim()
        if (trimmed.isBlank()) {
            return SearchUsersResult(emptyList(), null)
        }

        // Local lookup for instant results (cached / user-created).
        val localResults = fetchLocalUsers(trimmed)

        // Remote lookup for network-backed results (persisted to Room).
        var errorMessage: String? = null
        val remoteResults = try {
            fetchRemoteUsers(trimmed)
        } catch (error: Exception) {
            errorMessage = error.message ?: "Couldn't load search results."
            emptyList()
        }
        if (remoteResults.isNotEmpty()) {
            // Persist to DB entities.
            userDao.upsertAll(remoteResults)
        }

        // Merge + dedupe by id.
        return SearchUsersResult(
            users = (localResults + remoteResults).distinctBy { it.id },
            errorMessage = errorMessage
        )
    }

    private suspend fun fetchLocalUsers(query: String): List<UserEntity> {
        return try {
            userDao.searchByUsername(query.lowercase()).first()
        } catch (_: Exception) {
            emptyList()
        }
    }

    private suspend fun fetchRemoteUsers(query: String): List<UserEntity> {
        return userApi.searchUsers(query).users.map { remote ->
            remote.toEntity()
        }
    }

    private fun RemoteUserDto.toEntity(): UserEntity {
        val bio = buildBio(
            city = address.city,
            country = address.country,
            university = university,
            companyName = company.name
        )
        return UserEntity(
            id = id,
            username = username,
            name = "$firstName $lastName".trim(),
            email = email,
            avatarUrl = "https://i.pravatar.cc/150?u=$username",
            bio = bio,
            followersCount = 0,
            followingCount = 0,
            postsCount = 0
        )
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

/**
 * Search result payload including any non-fatal error message.
 */
data class SearchUsersResult(
    val users: List<UserEntity>,
    val errorMessage: String?
)
