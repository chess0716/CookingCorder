package com.example.ccp.model




data class User(
    val id: Long?,
    val username: String,
    val name: String?,
    val password: String?,
    val email: String?,

    val boards: Collection<BoardDTO>?
)
data class UserResponse(
    var message: String
)

data class LoginRequest(
    var username: String,
    var password: String
)

data class LoginResponse(
    var message: String
)


