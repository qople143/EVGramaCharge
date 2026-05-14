package com.evgramacharge.app.data.model

enum class SocketType { FIVE_A, FIFTEEN_A }
enum class BookingStatus { REQUEST_SENT, ACCEPTED, CANCELLED, COMPLETED }

data class ChargingPoint(
    val id: String = "",
    val hostId: String = "",
    val name: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val socketType: SocketType = SocketType.FIVE_A,
    val pricePerHour: Int = 0,
    val isAvailable: Boolean = true,
    val operatingHours: String = "",
    val rating: Double = 0.0,
    val reviewCount: Int = 0,
    val imageUrl: String? = null
)

data class User(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val isHost: Boolean = false,
    val profilePictureUrl: String? = null,
    val preferences: String = ""
)

data class Booking(
    val id: String = "",
    val riderId: String = "",
    val chargingPointId: String = "",
    val status: BookingStatus = BookingStatus.REQUEST_SENT,
    val timestamp: Long = 0L,
    val rating: Int? = null,
    val review: String? = null
)

sealed class UiState<out T> {
    object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
}
