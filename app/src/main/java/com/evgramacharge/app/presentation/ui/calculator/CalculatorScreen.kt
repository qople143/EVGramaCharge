package com.evgramacharge.app.presentation.ui.calculator

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.expandVertically
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.evgramacharge.app.data.model.UiState
import com.evgramacharge.app.presentation.viewmodel.CalculatorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(
    modifier: Modifier = Modifier,
    viewModel: CalculatorViewModel = hiltViewModel()
) {
    var batteryCap by remember { mutableStateOf("3.0") }
    var currentCharge by remember { mutableStateOf("20") }
    var efficiency by remember { mutableStateOf("30") }
    var selectedAmpIndex by remember { mutableIntStateOf(0) }
    val ampOptions = listOf(5, 15)

    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .imePadding()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .size(150.dp)
                .padding(bottom = 8.dp)
        ) {
            coil.compose.AsyncImage(
                model = "https://images.unsplash.com/photo-1554224155-8d04cb21cd6c?w=400&q=80",
                contentDescription = "Calculator Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp))
            )
        }

        Text("Charge Estimator (30m)", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        OutlinedTextField(
            value = batteryCap,
            onValueChange = { batteryCap = it },
            label = { Text("Battery Capacity (kWh)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )
        
        OutlinedTextField(
            value = currentCharge,
            onValueChange = { currentCharge = it },
            label = { Text("Current Charge (%)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Next),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = efficiency,
            onValueChange = { efficiency = it },
            label = { Text("Vehicle Efficiency (km/kWh)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done),
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        )

        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            ampOptions.forEachIndexed { index, amp ->
                SegmentedButton(
                    selected = index == selectedAmpIndex,
                    onClick = { selectedAmpIndex = index },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("${amp}A Socket")
                }
            }
        }

        Button(
            onClick = {
                viewModel.calculateCharge(
                    batteryCap.toDoubleOrNull() ?: 0.0,
                    currentCharge.toDoubleOrNull() ?: 0.0,
                    ampOptions[selectedAmpIndex],
                    efficiency.toDoubleOrNull() ?: 0.0
                )
            },
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        ) {
            Text("Calculate", modifier = Modifier.padding(vertical = 8.dp), fontWeight = FontWeight.Bold)
        }

        if (uiState is UiState.Error) {
            Text((uiState as UiState.Error).message, color = MaterialTheme.colorScheme.error)
        } else if (uiState is UiState.Success) {
            val res = (uiState as UiState.Success).data
            AnimatedVisibility(
                visible = res.energyKwh > 0,
                enter = fadeIn() + expandVertically()
            ) {
                ElevatedCard(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Estimated Addition in 30 mins", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            // Battery Metric
                            MetricColumn(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Star,
                                value = "+${"%.1f".format(res.chargeGainedPct)}%",
                                label = "Battery"
                            )

                            // Range Metric
                            MetricColumn(
                                modifier = Modifier.weight(1f),
                                icon = Icons.Default.Info,
                                value = "+${"%.1f".format(res.rangeAddedKm)}",
                                label = "km Range"
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                            SecondaryText("Power: ${"%.2f".format(res.powerKw)} kW")
                            SecondaryText("Energy: ${"%.2f".format(res.energyKwh)} kWh")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetricColumn(modifier: Modifier, icon: androidx.compose.ui.graphics.vector.ImageVector, value: String, label: String) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(28.dp))
        Spacer(modifier = Modifier.height(4.dp))
        Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold, color = MaterialTheme.colorScheme.primary)
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimaryContainer)
    }
}

@Composable
fun SecondaryText(text: String) {
    Text(text, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))
}
