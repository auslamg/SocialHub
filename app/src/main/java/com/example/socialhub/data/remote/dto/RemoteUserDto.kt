package com.example.socialhub.data.remote.dto

/**
 * Response wrapper for user search/list endpoints.
 */
data class RemoteUsersResponse(
    val users: List<RemoteUserDto>
)

/**
 * User payload returned by the remote API.
 */
data class RemoteUserDto(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val username: String,
    val email: String,
    val address: RemoteUserAddressDto,
    val university: String,
    val company: RemoteUserCompanyDto
)

/**
 * Nested address payload for a remote user.
 */
data class RemoteUserAddressDto(
    val city: String,
    val country: String
)

/**
 * Nested company payload for a remote user.
 */
data class RemoteUserCompanyDto(
    val name: String
)
