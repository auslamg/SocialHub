package com.example.socialhub.data.repository

import com.example.socialhub.data.local.dao.UserDao
import com.example.socialhub.data.local.entity.UserEntity
import com.example.socialhub.data.remote.api.UserApi
import com.example.socialhub.data.remote.dto.RemoteUserDto
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Singleton
class UserRepository @Inject constructor(
    private val userDao: UserDao,
    private val userApi: UserApi
) {
    private val fetchedUserIds = mutableSetOf<Long>()

    fun observeUsers(): Flow<List<UserEntity>> = userDao.observeUsers()

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

data class SearchUsersResult(
    val users: List<UserEntity>,
    val errorMessage: String?
)
