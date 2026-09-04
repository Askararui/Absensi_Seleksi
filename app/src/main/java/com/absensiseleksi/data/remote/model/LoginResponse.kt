package com.absensiseleksi.data.remote.model

data class LoginResponse(
    val token: String,
    val userId: String,
    val name: String
)