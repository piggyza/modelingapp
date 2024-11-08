package com.example.modelbookingapp.data.model

data class User(
    val id: String = "",
    val email: String = "",
    val userType: UserType? = null,
    val name: String = "",
    val profileCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)