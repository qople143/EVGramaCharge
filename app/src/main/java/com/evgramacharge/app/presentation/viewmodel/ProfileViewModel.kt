package com.evgramacharge.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evgramacharge.app.data.model.Booking
import com.evgramacharge.app.data.model.BookingStatus
import com.evgramacharge.app.data.model.ChargingPoint
import com.evgramacharge.app.data.model.SocketType
import com.evgramacharge.app.data.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {

    private val _hostPoints = MutableStateFlow<List<ChargingPoint>>(emptyList())
    val hostPoints: StateFlow<List<ChargingPoint>> = _hostPoints.asStateFlow()

    private val _userBookings = MutableStateFlow<List<Booking>>(emptyList())
    val userBookings: StateFlow<List<Booking>> = _userBookings.asStateFlow()

    private val _hostBookings = MutableStateFlow<List<Booking>>(emptyList())
    val hostBookings: StateFlow<List<Booking>> = _hostBookings.asStateFlow()

    fun loadHostData(hostId: String) {
        viewModelScope.launch {
            repository.getChargingPointsByHost(hostId).collect {
                _hostPoints.value = it
            }
        }
        viewModelScope.launch {
            repository.getBookingsForHost(hostId).collect {
                _hostBookings.value = it
            }
        }
    }

    fun loadUserData(userId: String) {
        viewModelScope.launch {
            repository.getBookingsForUser(userId).collect {
                _userBookings.value = it
            }
        }
    }

    fun savePoint(point: ChargingPoint) {
        viewModelScope.launch {
            repository.saveChargingPoint(point)
        }
    }

    fun deletePoint(id: String) {
        viewModelScope.launch {
            repository.deleteChargingPoint(id)
        }
    }

    fun updateBooking(bookingId: String, status: BookingStatus) {
        viewModelScope.launch {
            repository.updateBookingStatus(bookingId, status)
        }
    }

    fun reviewBooking(bookingId: String, rating: Int, review: String) {
        viewModelScope.launch {
            repository.reviewBooking(bookingId, rating, review)
        }
    }
}
