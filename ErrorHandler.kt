package com.example.modelbookingapp.utils

sealed class AppError : Exception() {
    object NetworkError : AppError()
    object AuthenticationError : AppError()
    object ValidationError : AppError()
    data class ServerError(override val message: String) : AppError()
    data class UnknownError(override val message: String) : AppError()
}

fun handleError(error: Exception): AppError {
    return when (error) {
        is AppError -> error
        is java.net.UnknownHostException -> AppError.NetworkError
        is com.google.firebase.FirebaseException -> AppError.ServerError(error.message ?: "Server error")
        else -> AppError.UnknownError(error.message ?: "Unknown error occurred")
    }
}