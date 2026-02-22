package com.example.socialhub.data.remote.api

import com.example.socialhub.data.remote.dto.RemoteUserDto
import com.example.socialhub.data.remote.dto.RemoteUsersResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface UserApi {
    @GET("users")
    suspend fun getUsers(@Query("limit") limit: Int = 10): RemoteUsersResponse

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: Long): RemoteUserDto
}
