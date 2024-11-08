package com.example.modelbookingapp.ui.screens.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.modelbookingapp.data.model.Booking
import com.example.modelbookingapp.data.model.BookingStatus
import com.example.modelbookingapp.data.model.ModelProfile
import com.example.modelbookingapp.data.model.Resource
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AdminViewModel @Inject constructor(
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _allModelsState = MutableStateFlow<Resource<List<ModelProfile>>>(Resource.Loading)
    val allModelsState: StateFlow<Resource<List<ModelProfile>>> = _allModelsState

    private val _allBookingsState = MutableStateFlow<Resource<List<Booking>>>(Resource.Loading)
    val allBookingsState: StateFlow<Resource<List<Booking>>> = _allBookingsState

    init {
        loadAllModels()
        loadAllBookings()
    }

    private fun loadAllModels() {
        viewModelScope.launch {
            try {
                _allModelsState.value = Resource.Loading

                val models = firestore.collection("models")
                    .get()
                    .await()
                    .documents
                    .mapNotNull { doc ->
                        doc.toObject(ModelProfile::class.java)?.copy(id = doc.id)
                    }

                _allModelsState.value = Resource.Success(models)
            } catch (e: Exception) {
                _allModelsState.value = Resource.Error(e.message ?: "Failed to load models")
            }
        }
    }

    private fun loadAllBookings() {
        viewModelScope.launch {
            try {
                _allBookingsState.value = Resource.Loading

                val bookings = firestore.collection("bookings")
                    .get()
                    .await()
                    .toObjects(Booking::class.java)

                _allBookingsState.value = Resource.Success(bookings)
            } catch (e: Exception) {
                _allBookingsState.value = Resource.Error(e.message ?: "Failed to load bookings")
            }
        }
    }

    fun updateBookingStatus(bookingId: String, status: BookingStatus) {
        viewModelScope.launch {
            try {
                firestore.collection("bookings")
                    .document(bookingId)
                    .update("status", status.name)
                    .await()

                loadAllBookings() // Refresh the bookings list
            } catch (e: Exception) {
                _allBookingsState.value = Resource.Error(e.message ?: "Failed to update booking status")
            }
        }
    }

    fun refreshData() {
        loadAllModels()
        loadAllBookings()
    }
}