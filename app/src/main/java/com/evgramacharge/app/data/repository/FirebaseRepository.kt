package com.evgramacharge.app.data.repository

import com.evgramacharge.app.data.model.Booking
import com.evgramacharge.app.data.model.ChargingPoint
import com.evgramacharge.app.data.model.User
import kotlinx.coroutines.flow.Flow

interface FirebaseRepository {
    fun getChargingPoints(): Flow<List<ChargingPoint>>
    suspend fun getChargingPointById(id: String): ChargingPoint?
    suspend fun createBooking(booking: Booking): Boolean
    suspend fun getUserProfile(uid: String): User?
    suspend fun saveUserProfile(user: User): Boolean
    suspend fun saveChargingPoint(point: ChargingPoint): Boolean
    suspend fun deleteChargingPoint(id: String): Boolean
    fun getChargingPointsByHost(hostId: String): Flow<List<ChargingPoint>>
    fun getBookingsForUser(userId: String): Flow<List<Booking>>
    fun getBookingsForHost(hostId: String): Flow<List<Booking>>
    suspend fun updateBookingStatus(bookingId: String, status: com.evgramacharge.app.data.model.BookingStatus): Boolean
    suspend fun reviewBooking(bookingId: String, rating: Int, review: String): Boolean
}
