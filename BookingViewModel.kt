package com.example.modelbookingapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.modelbookingapp.data.model.Booking
import com.example.modelbookingapp.data.model.BookingStatus
import com.example.modelbookingapp.data.repository.BookingRepository
import com.example.modelbookingapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val repository: BookingRepository
) : ViewModel() {

    private val _bookingsState = MutableStateFlow<Resource<List<Booking>>>(Resource.Loading)
    val bookingsState: StateFlow<Resource<List<Booking>>> = _bookingsState

    private val _selectedBooking = MutableStateFlow<Resource<Booking>>(Resource.Loading)
    val selectedBooking: StateFlow<Resource<Booking>> = _selectedBooking

    fun loadBookingsForModel(modelId: String) {
        viewModelScope.launch {
            _bookingsState.value = Resource.Loading
            _bookingsState.value = repository.getBookingsForModel(modelId)
        }
    }

    fun loadBookingsForClient(clientId: String) {
        viewModelScope.launch {
            _bookingsState.value = Resource.Loading
            _bookingsState.value = repository.getBookingsForClient(clientId)
        }
    }

    fun loadAllBookings() {
        viewModelScope.launch {
            _bookingsState.value = Resource.Loading
            _bookingsState.value = repository.getAllBookings()
        }
    }

    fun createBooking(
        modelId: String,
        clientId: String,
        date: Long,
        duration: Int,
        location: String,
        description: String,
        requirements: String,
        totalAmount: Double
    ) {
        viewModelScope.launch {
            val booking = Booking(
                modelId = modelId,
                clientId = clientId,
                date = date,
                duration = duration,
                location = location,
                description = description,
                requirements = requirements,
                totalAmount = totalAmount
            )
            val result = repository.createBooking(booking)
            if (result is Resource.Success) {
                loadBookingsForClient(clientId)
            }
        }
    }

    fun updateBookingStatus(bookingId: String, status: BookingStatus) {
        viewModelScope.launch {
            val result = repository.updateBookingStatus(bookingId, status)
            if (result is Resource.Success) {
                refreshBookingDetails(bookingId)
            }
        }
    }

    fun getBookingDetails(bookingId: String) {
        viewModelScope.launch {
            _selectedBooking.value = Resource.Loading
            _selectedBooking.value = repository.getBooking(bookingId)
        }
    }

    private fun refreshBookingDetails(bookingId: String) {
        viewModelScope.launch {
            _selectedBooking.value = repository.getBooking(bookingId)
        }
    }
}