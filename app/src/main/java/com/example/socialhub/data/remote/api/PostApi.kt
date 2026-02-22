package com.example.socialhub.data.remote.api

import com.example.socialhub.data.remote.dto.RemotePostDto
import retrofit2.http.GET
import retrofit2.http.Query

interface PostApi {
    @GET("posts")
    suspend fun getPosts(@Query("_limit") limit: Int = 20): List<RemotePostDto>
}
