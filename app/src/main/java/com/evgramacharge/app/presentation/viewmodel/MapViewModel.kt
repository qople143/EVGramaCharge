package com.evgramacharge.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evgramacharge.app.data.model.ChargingPoint
import com.evgramacharge.app.data.model.UiState
import com.evgramacharge.app.data.model.SocketType
import com.evgramacharge.app.data.repository.FirebaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MapViewModel @Inject constructor(
    private val repository: FirebaseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<List<ChargingPoint>>>(UiState.Loading)
    val uiState: StateFlow<UiState<List<ChargingPoint>>> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _socketFilter = MutableStateFlow<SocketType?>(null)
    val socketFilter: StateFlow<SocketType?> = _socketFilter.asStateFlow()

    private val _maxPriceFilter = MutableStateFlow<Int?>(null)
    val maxPriceFilter: StateFlow<Int?> = _maxPriceFilter.asStateFlow()

    private val _bookingState = MutableStateFlow<UiState<Boolean>?>(null)
    val bookingState: StateFlow<UiState<Boolean>?> = _bookingState.asStateFlow()

    init {
        fetchChargingPoints()
    }

    private fun fetchChargingPoints() {
        viewModelScope.launch {
            _uiState.value = UiState.Loading
            combine(
                repository.getChargingPoints(),
                _searchQuery,
                _socketFilter,
                _maxPriceFilter
            ) { points, query, socket, maxPrice ->
                points.filter { point ->
                    val matchesQuery = query.isEmpty() || point.name.contains(query, ignoreCase = true)
                    val matchesSocket = socket == null || point.socketType == socket
                    val matchesPrice = maxPrice == null || point.pricePerHour <= maxPrice
                    matchesQuery && matchesSocket && matchesPrice
                }
            }
            .catch { e -> _uiState.value = UiState.Error(e.message ?: "Error fetching points") }
            .collect { filteredPoints ->
                _uiState.value = UiState.Success(filteredPoints)
            }
        }
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setSocketFilter(socket: SocketType?) {
        // Update socket filter state
        _socketFilter.value = socket
    }

    fun setMaxPrice(price: Int?) {
        _maxPriceFilter.value = price
    }

    fun bookPoint(point: ChargingPoint, userId: String) {
        viewModelScope.launch {
            _bookingState.value = UiState.Loading
            try {
                val booking = com.evgramacharge.app.data.model.Booking(
                    id = java.util.UUID.randomUUID().toString(),
                    riderId = userId,
                    chargingPointId = point.id,
                    status = com.evgramacharge.app.data.model.BookingStatus.REQUEST_SENT,
                    timestamp = System.currentTimeMillis()
                )
                val success = repository.createBooking(booking)
                if (success) {
                    _bookingState.value = UiState.Success(true)
                } else {
                    _bookingState.value = UiState.Error("Booking failed")
                }
            } catch (e: Exception) {
                _bookingState.value = UiState.Error(e.message ?: "Booking failed")
            }
        }
    }

    fun resetBookingState() {
        _bookingState.value = null
    }
}
