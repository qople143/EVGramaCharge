package com.evgramacharge.app.data.repository

import com.evgramacharge.app.data.model.Booking
import com.evgramacharge.app.data.model.BookingStatus
import com.evgramacharge.app.data.model.ChargingPoint
import com.evgramacharge.app.data.model.SocketType
import com.evgramacharge.app.data.model.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import java.util.UUID

class FirebaseRepositoryImpl @Inject constructor() : FirebaseRepository {

    private val usersFlow = MutableStateFlow<List<User>>(emptyList())
    
    private val pointsFlow = MutableStateFlow(
        listOf(
            ChargingPoint("seed1", "host_demo_1", "Raju's Kirana Store", 12.9352, 77.5946, SocketType.FIVE_A, 20, true, "9AM-9PM"),
            ChargingPoint("seed2", "host_demo_2", "Lakshmi Tea Stall", 12.9784, 77.6408, SocketType.FIFTEEN_A, 50, true, "6AM-10PM"),
            ChargingPoint("seed3", "host_demo_3", "Venkat Electronics", 12.9308, 77.5838, SocketType.FIVE_A, 25, false, "10AM-8PM"),
            ChargingPoint("seed4", "host_demo_4", "Ganesha Hardware", 12.9969, 77.5891, SocketType.FIFTEEN_A, 60, true, "9AM-7PM"),
            ChargingPoint("seed5", "host_demo_5", "Kiran Cycle Clinic", 12.9166, 77.6101, SocketType.FIVE_A, 20, true, "8AM-8PM"),
            ChargingPoint("seed6", "host_demo_6", "Sri Ram Digital Shop", 12.9698, 77.7500, SocketType.FIVE_A, 30, false, "10AM-10PM"),
            ChargingPoint("seed7", "host_demo_7", "Priyanka Bakery & Sweets", 12.9400, 77.6200, SocketType.FIVE_A, 22, true, "7AM-9PM"),
            ChargingPoint("seed8", "host_demo_8", "Nandi Medicals", 12.9800, 77.6000, SocketType.FIFTEEN_A, 45, true, "24/7"),
            ChargingPoint("seed9", "host_demo_9", "Venkateshwara Provision Store", 12.9200, 77.6500, SocketType.FIVE_A, 18, true, "8AM-10PM"),
            ChargingPoint("seed10", "host_demo_10", "Murugan Auto Parts", 12.9500, 77.5500, SocketType.FIFTEEN_A, 55, false, "9AM-8PM"),
            ChargingPoint("seed11", "host_demo_11", "Shiva's Mini Mart", 12.9100, 77.5300, SocketType.FIVE_A, 15, true, "8AM-11PM"),
            ChargingPoint("seed12", "host_demo_12", "Bhavani Stores", 12.9850, 77.5550, SocketType.FIFTEEN_A, 40, true, "7AM-9PM"),
            ChargingPoint("seed13", "host_demo_13", "Anand Sweets & Snacks", 12.9250, 77.6250, SocketType.FIVE_A, 22, true, "10AM-10PM"),
            ChargingPoint("seed14", "host_demo_14", "Ravi Mobile Shop", 12.9650, 77.6350, SocketType.FIVE_A, 18, false, "10AM-8PM"),
            ChargingPoint("seed15", "host_demo_15", "Shanti Medical & General", 12.9750, 77.5750, SocketType.FIFTEEN_A, 50, true, "24/7"),
            ChargingPoint("seed16", "host_demo_16", "Ganesh Coffee Bar", 12.9450, 77.5850, SocketType.FIVE_A, 20, true, "6AM-8PM"),
            ChargingPoint("seed17", "host_demo_17", "Sri Krishna Enterprises", 12.9550, 77.6150, SocketType.FIFTEEN_A, 45, false, "9AM-6PM"),
            ChargingPoint("seed18", "host_demo_18", "Raghavendra Provision", 12.9050, 77.5950, SocketType.FIVE_A, 25, true, "8AM-9PM"),
            ChargingPoint("seed19", "host_demo_19", "Maha Lakshmi Fancy Center", 12.9150, 77.5650, SocketType.FIVE_A, 20, true, "10AM-9PM"),
            ChargingPoint("seed20", "host_demo_20", "Balaji Tyres & Quick Charge", 12.9350, 77.5450, SocketType.FIFTEEN_A, 60, true, "9AM-7PM"),
            ChargingPoint("seed21", "host_demo_21", "Sriram Provision Store", 13.0326, 77.5868, SocketType.FIVE_A, 15, true, "8AM-10PM"),
            ChargingPoint("seed22", "host_demo_22", "Lakshmi Flour Mill", 13.0340, 77.5880, SocketType.FIFTEEN_A, 40, true, "9AM-8PM"),
            ChargingPoint("seed23", "host_demo_23", "Kavya General Store", 13.0350, 77.5890, SocketType.FIVE_A, 20, true, "7AM-9PM"),
            ChargingPoint("seed24", "host_demo_24", "Maruthi Super Market", 13.0310, 77.5850, SocketType.FIFTEEN_A, 45, false, "8AM-11PM"),
            ChargingPoint("seed25", "host_demo_25", "Bhavani Dairy & Sweets", 13.0330, 77.5860, SocketType.FIVE_A, 18, true, "6AM-10PM"),
            ChargingPoint("seed26", "host_demo_26", "Ganesh Kirana Shop", 13.0345, 77.5875, SocketType.FIVE_A, 16, true, "9AM-9PM"),
            ChargingPoint("seed27", "host_demo_27", "Ananya Mini Mart", 13.0320, 77.5888, SocketType.FIVE_A, 22, true, "8AM-10PM"),
            ChargingPoint("seed28", "host_demo_28", "Raghav Provision", 13.0355, 77.5865, SocketType.FIFTEEN_A, 50, true, "7AM-9PM"),
            ChargingPoint("seed29", "host_demo_29", "Srikanth Daily Needs", 13.0315, 77.5872, SocketType.FIVE_A, 25, false, "8AM-8PM"),
            ChargingPoint("seed30", "host_demo_30", "Manoj Wholesale", 13.0360, 77.5885, SocketType.FIFTEEN_A, 55, true, "9AM-7PM"),
            ChargingPoint("seed31", "host_demo_31", "Sushma Bakery", 13.0325, 77.5855, SocketType.FIVE_A, 15, true, "7AM-9PM"),
            ChargingPoint("seed32", "host_demo_32", "Nandini Milk Parlour", 13.0348, 77.5895, SocketType.FIVE_A, 20, true, "6AM-10PM"),
            ChargingPoint("seed33", "host_demo_33", "Karthik General", 13.0335, 77.5858, SocketType.FIFTEEN_A, 45, true, "8AM-9PM"),
            ChargingPoint("seed34", "host_demo_34", "Vidya Stationeries", 13.0318, 77.5892, SocketType.FIVE_A, 18, false, "9AM-8PM"),
            ChargingPoint("seed35", "host_demo_35", "Bharath Fresh Mart", 13.0352, 77.5862, SocketType.FIVE_A, 24, true, "7AM-11PM"),
            ChargingPoint("seed36", "host_demo_36", "Arun Ice Creams", 13.0328, 77.5882, SocketType.FIFTEEN_A, 60, true, "10AM-10PM"),
            ChargingPoint("seed37", "host_demo_37", "Priya Provisions", 13.0342, 77.5852, SocketType.FIVE_A, 20, true, "8AM-9PM"),
            ChargingPoint("seed38", "host_demo_38", "Venkatesh Stores", 13.0312, 77.5878, SocketType.FIVE_A, 22, false, "7AM-9PM"),
            ChargingPoint("seed39", "host_demo_39", "Surya Vegetables", 13.0358, 77.5898, SocketType.FIFTEEN_A, 35, true, "6AM-8PM"),
            ChargingPoint("seed40", "host_demo_40", "Deepa Mini Shop", 13.0332, 77.5868, SocketType.FIVE_A, 15, true, "9AM-9PM"),
            ChargingPoint("seed41", "host_demo_41", "Raju Tea & Snacks", 13.0346, 77.5886, SocketType.FIVE_A, 18, true, "7AM-10PM"),
            ChargingPoint("seed42", "host_demo_42", "Prashanth Retail", 13.0322, 77.5858, SocketType.FIFTEEN_A, 42, false, "8AM-9PM"),
            ChargingPoint("seed43", "host_demo_43", "Guru Traders", 13.0354, 77.5894, SocketType.FIVE_A, 26, true, "9AM-8PM"),
            ChargingPoint("seed44", "host_demo_44", "Asha Super Mart", 13.0316, 77.5864, SocketType.FIVE_A, 30, true, "7AM-10PM"),
            ChargingPoint("seed45", "host_demo_45", "Uday Grocery", 13.0338, 77.5888, SocketType.FIFTEEN_A, 50, true, "8AM-9PM"),
            ChargingPoint("seed46", "host_demo_46", "Kiran Consumer Shop", 13.0349, 77.5859, SocketType.FIVE_A, 19, false, "9AM-9PM"),
            ChargingPoint("seed47", "host_demo_47", "Suresh Retailers", 13.0324, 77.5896, SocketType.FIVE_A, 21, true, "8AM-8PM"),
            ChargingPoint("seed48", "host_demo_48", "Mahesh Stores", 13.0356, 77.5866, SocketType.FIFTEEN_A, 65, true, "7AM-10PM"),
            ChargingPoint("seed49", "host_demo_49", "Vijay General Store", 13.0314, 77.5884, SocketType.FIVE_A, 25, true, "8AM-9PM"),
            ChargingPoint("seed50", "host_demo_50", "Sneha Daily Mart", 13.0339, 77.5879, SocketType.FIVE_A, 18, false, "9AM-8PM")
        )
    )

    private val bookingsFlow = MutableStateFlow<List<Booking>>(emptyList())

    init {
        val database = com.google.firebase.database.FirebaseDatabase.getInstance().reference.child("charging_points")
        database.addListenerForSingleValueEvent(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                if (!snapshot.exists() || snapshot.childrenCount < 10) {
                    pointsFlow.value.forEach { 
                        database.child(it.id).setValue(it)
                    }
                } else {
                    val list = mutableListOf<ChargingPoint>()
                    snapshot.children.forEach { child ->
                        val item = child.getValue(ChargingPoint::class.java)
                        if (item != null) list.add(item)
                    }
                    if (list.isNotEmpty()) {
                        pointsFlow.value = list
                    }
                }
            }
            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
        })
    }

    override fun getChargingPoints(): Flow<List<ChargingPoint>> = pointsFlow

    override suspend fun getChargingPointById(id: String): ChargingPoint? {
        return pointsFlow.value.find { it.id == id }
    }

    override suspend fun createBooking(booking: Booking): Boolean {
        // Automatically make point busy when requested
        val currentPoints = pointsFlow.value.toMutableList()
        val index = currentPoints.indexOfFirst { it.id == booking.chargingPointId }
        if (index != -1) {
            currentPoints[index] = currentPoints[index].copy(isAvailable = false)
            pointsFlow.value = currentPoints
        }

        val newBooking = booking.copy(id = UUID.randomUUID().toString(), timestamp = System.currentTimeMillis())
        bookingsFlow.value = bookingsFlow.value + newBooking
        
        return true
    }

    override suspend fun getUserProfile(uid: String): User? {
        val inMemory = usersFlow.value.find { it.uid == uid }
        if (inMemory != null) return inMemory
        
        val authUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
        if (authUser != null && authUser.uid == uid) {
            val user = User(
                uid = uid, 
                name = authUser.displayName ?: "User", 
                email = authUser.email ?: "", 
                isHost = false
            )
            val current = usersFlow.value.toMutableList()
            current.add(user)
            usersFlow.value = current
            return user
        }
        
        return User(uid = uid, name = "Test User", email = "test@user.com", isHost = false)
    }

    override suspend fun saveUserProfile(user: User): Boolean {
        val current = usersFlow.value.toMutableList()
        val idx = current.indexOfFirst { it.uid == user.uid }
        if (idx != -1) {
            current[idx] = user
        } else {
            current.add(user)
        }
        usersFlow.value = current
        return true
    }
    
    override suspend fun saveChargingPoint(point: ChargingPoint): Boolean {
        val current = pointsFlow.value.toMutableList()
        val idx = current.indexOfFirst { it.id == point.id }
        if (idx != -1) {
            current[idx] = point
        } else {
            current.add(point.copy(id = UUID.randomUUID().toString()))
        }
        pointsFlow.value = current
        return true
    }

    override suspend fun deleteChargingPoint(id: String): Boolean {
        pointsFlow.value = pointsFlow.value.filter { it.id != id }
        return true
    }

    override fun getChargingPointsByHost(hostId: String): Flow<List<ChargingPoint>> {
        return pointsFlow.map { list -> list.filter { it.hostId == hostId } }
    }

    override fun getBookingsForUser(userId: String): Flow<List<Booking>> {
        return bookingsFlow.map { list -> list.filter { it.riderId == userId } }
    }

    override fun getBookingsForHost(hostId: String): Flow<List<Booking>> {
        // Find points for this host first
        return bookingsFlow.map { bookings ->
            val hostPointIds = pointsFlow.value.filter { it.hostId == hostId }.map { it.id }.toSet()
            bookings.filter { it.chargingPointId in hostPointIds }
        }
    }

    override suspend fun updateBookingStatus(bookingId: String, status: BookingStatus): Boolean {
        val current = bookingsFlow.value.toMutableList()
        val idx = current.indexOfFirst { it.id == bookingId }
        if (idx != -1) {
            val booking = current[idx]
            current[idx] = booking.copy(status = status)
            bookingsFlow.value = current
            
            // If completed or cancelled, make point available again
            if (status == BookingStatus.COMPLETED || status == BookingStatus.CANCELLED) {
                val pointCurrent = pointsFlow.value.toMutableList()
                val pointIdx = pointCurrent.indexOfFirst { it.id == booking.chargingPointId }
                if (pointIdx != -1) {
                    pointCurrent[pointIdx] = pointCurrent[pointIdx].copy(isAvailable = true)
                    pointsFlow.value = pointCurrent
                }
            } else if (status == BookingStatus.ACCEPTED) {
                val pointCurrent = pointsFlow.value.toMutableList()
                val pointIdx = pointCurrent.indexOfFirst { it.id == booking.chargingPointId }
                if (pointIdx != -1) {
                    pointCurrent[pointIdx] = pointCurrent[pointIdx].copy(isAvailable = false)
                    pointsFlow.value = pointCurrent
                }
            }
        }
        return true
    }

    override suspend fun reviewBooking(bookingId: String, rating: Int, review: String): Boolean {
        val current = bookingsFlow.value.toMutableList()
        val idx = current.indexOfFirst { it.id == bookingId }
        if (idx != -1) {
            val booking = current[idx]
            current[idx] = booking.copy(rating = rating, review = review, status = BookingStatus.COMPLETED)
            bookingsFlow.value = current

            val pointCurrent = pointsFlow.value.toMutableList()
            val pointIdx = pointCurrent.indexOfFirst { it.id == booking.chargingPointId }
            if (pointIdx != -1) {
                val point = pointCurrent[pointIdx]
                val newCount = point.reviewCount + 1
                val newRating = ((point.rating * point.reviewCount) + rating) / newCount
                pointCurrent[pointIdx] = point.copy(
                    isAvailable = true,
                    rating = newRating,
                    reviewCount = newCount
                )
                pointsFlow.value = pointCurrent
            }
            return true
        }
        return false
    }
}
