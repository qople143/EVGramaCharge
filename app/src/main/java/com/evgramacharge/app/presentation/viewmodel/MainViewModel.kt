package com.evgramacharge.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evgramacharge.app.data.model.Booking
import com.evgramacharge.app.data.model.BookingStatus
import com.evgramacharge.app.data.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {

    private val _notifications = MutableSharedFlow<String>()
    val notifications: SharedFlow<String> = _notifications.asSharedFlow()

    private var previousBookings: List<Booking> = emptyList()

    fun listenForBookings(userId: String, isHost: Boolean) {
        viewModelScope.launch {
            val flow = if (isHost) {
                repository.getBookingsForHost(userId)
            } else {
                repository.getBookingsForUser(userId)
            }

            flow.collect { bookings ->
                if (previousBookings.isNotEmpty()) {
                    bookings.forEach { newBooking ->
                        val oldBooking = previousBookings.find { it.id == newBooking.id }
                        if (oldBooking != null && oldBooking.status != newBooking.status) {
                            if (!isHost && newBooking.status == BookingStatus.ACCEPTED) {
                                _notifications.emit("Your booking at ${newBooking.chargingPointId} was accepted!")
                            } else if (isHost && newBooking.status == BookingStatus.REQUEST_SENT && oldBooking == null) {
                                _notifications.emit("New booking request received for point ${newBooking.chargingPointId}!")
                            }
                        } else if (oldBooking == null && isHost) {
                             _notifications.emit("New booking request received for point ${newBooking.chargingPointId}!")
                        }
                    }
                }
                previousBookings = bookings
            }
        }
    }
}
