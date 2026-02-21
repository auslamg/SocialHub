package com.example.socialhub.data.remote.api

import com.example.socialhub.data.remote.dto.RemoteUserDto
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApi {
    @GET("users")
    suspend fun getUsers(@Query("_limit") limit: Int = 10): List<RemoteUserDto>
}
