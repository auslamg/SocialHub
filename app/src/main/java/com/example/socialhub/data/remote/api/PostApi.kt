package com.example.socialhub.data.remote.api

import com.example.socialhub.data.remote.dto.RemotePostsResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API for post endpoints.
 */
interface PostApi {
    /**
     * Fetches recent posts for the timeline.
     */
    @GET("posts")
    suspend fun getPosts(@Query("limit") limit: Int = 20): RemotePostsResponse

    /**
     * Fetches posts for a specific user.
     */
    @GET("posts/user/{userId}")
    suspend fun getPostsByUser(@Path("userId") userId: Long): RemotePostsResponse
}
