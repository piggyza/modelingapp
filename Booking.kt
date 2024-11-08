package com.example.modelbookingapp.data.model

import java.util.Date

data class Booking(
    val id: String = "",
    val modelId: String = "",
    val clientId: String = "",
    val dateTime: Date = Date(),
    val duration: Int = 0,
    val status: BookingStatus = BookingStatus.PENDING,
    val notes: String = "",
    val totalAmount: Double = 0.0,
    val createdAt: Date = Date()
)