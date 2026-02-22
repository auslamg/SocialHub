package com.example.socialhub.data.remote.api

import com.example.socialhub.data.remote.dto.RemoteUserDto
import com.example.socialhub.data.remote.dto.RemoteUsersResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API for user endpoints.
 */
interface UserApi {
    /**
     * Fetches a page of users.
     */
    @GET("users")
    suspend fun getUsers(@Query("limit") limit: Int = 10): RemoteUsersResponse

    /**
     * Searches users by query string.
     */
    @GET("users/search")
    suspend fun searchUsers(@Query("q") query: String): RemoteUsersResponse

    /**
     * Fetches a single user by id.
     */
    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Long): RemoteUserDto
}
