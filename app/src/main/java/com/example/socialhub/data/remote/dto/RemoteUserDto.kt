package com.example.socialhub.data.remote.dto

data class RemoteUsersResponse(
    val users: List<RemoteUserDto>
)

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

data class RemoteUserAddressDto(
    val city: String,
    val country: String
)

data class RemoteUserCompanyDto(
    val name: String
)
