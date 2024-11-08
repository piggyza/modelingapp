package com.example.modelbookingapp.data.repository

import com.example.modelbookingapp.data.model.Booking
import com.example.modelbookingapp.data.model.BookingStatus
import com.example.modelbookingapp.data.model.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun createBooking(
        modelId: String,
        clientId: String,
        dateTime: Date,
        duration: Int,
        notes: String
    ): Flow<Resource<String>> = flow {
        try {
            emit(Resource.Loading)

            val booking = Booking(
                modelId = modelId,
                clientId = clientId,
                dateTime = dateTime,
                duration = duration,
                notes = notes,
                status = BookingStatus.PENDING,
                createdAt = Date()
            )

            val docRef = firestore.collection("bookings").add(booking).await()
            emit(Resource.Success(docRef.id))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun getBooking(bookingId: String): Flow<Resource<Booking>> = flow {
        try {
            emit(Resource.Loading)

            val booking = firestore.collection("bookings")
                .document(bookingId)
                .get()
                .await()
                .toObject(Booking::class.java)
                ?: throw Exception("Booking not found")

            emit(Resource.Success(booking))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun getBookingsForClient(clientId: String): Flow<Resource<List<Booking>>> = flow {
        try {
            emit(Resource.Loading)

            val bookings = firestore.collection("bookings")
                .whereEqualTo("clientId", clientId)
                .get()
                .await()
                .toObjects(Booking::class.java)

            emit(Resource.Success(bookings))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }

    fun getBookingsForModel(modelId: String): Flow<Resource<List<Booking>>> = flow {
        try {
            emit(Resource.Loading)

            val bookings = firestore.collection("bookings")
                .whereEqualTo("modelId", modelId)
                .get()
                .await()
                .toObjects(Booking::class.java)

            emit(Resource.Success(bookings))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Unknown error occurred"))
        }
    }
}