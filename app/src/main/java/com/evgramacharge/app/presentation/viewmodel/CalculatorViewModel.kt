package com.evgramacharge.app.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evgramacharge.app.data.model.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CalculatorResult(
    val powerKw: Double,
    val energyKwh: Double,
    val chargeGainedPct: Double,
    val rangeAddedKm: Double
)

@HiltViewModel
class CalculatorViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<CalculatorResult>>(UiState.Success(CalculatorResult(0.0, 0.0, 0.0, 0.0)))
    val uiState: StateFlow<UiState<CalculatorResult>> = _uiState.asStateFlow()

    fun calculateCharge(
        batteryCapacityKwh: Double,
        currentChargePct: Double,
        amperage: Int, // 5 or 15
        vehicleEfficiency: Double // km/kWh (25-70)
    ) {
        viewModelScope.launch {
            try {
                if (batteryCapacityKwh !in 1.0..10.0) throw IllegalArgumentException("Battery capacity must be 1-10 kWh")
                if (currentChargePct !in 0.0..99.0) throw IllegalArgumentException("Current charge must be 0-99%")
                if (vehicleEfficiency !in 25.0..70.0) throw IllegalArgumentException("Efficiency must be 25-70 km/kWh")

                // PRD Logic:
                // POWER_KW = (amperage * 220 * 0.85) / 1000
                // ENERGY_KWH = POWER_KW * 0.5 (for 30 minutes)
                // CHARGE_GAINED = (ENERGY_KWH / batteryCapacity) * 100
                // RANGE_KM = ENERGY_KWH * vehicleEfficiency

                val powerKw = (amperage * 220 * 0.85) / 1000.0
                val energyKwh = powerKw * 0.5
                val chargeGainedPct = (energyKwh / batteryCapacityKwh) * 100.0
                val rangeAddedKm = energyKwh * vehicleEfficiency

                _uiState.value = UiState.Success(
                    CalculatorResult(powerKw, energyKwh, chargeGainedPct, rangeAddedKm)
                )
            } catch (e: Exception) {
                _uiState.value = UiState.Error(e.message ?: "Unknown error")
            }
        }
    }
}
