package com.example.gopetalk_clean.data.api

data class LoginResponse(
    val email: String,
    val first_name: String,
    val last_name: String,
    val token: String,
    val user_id: Int
)