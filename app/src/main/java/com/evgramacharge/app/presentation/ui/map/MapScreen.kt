package com.evgramacharge.app.presentation.ui.map

import android.Manifest
import android.preference.PreferenceManager
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.evgramacharge.app.data.model.ChargingPoint
import com.evgramacharge.app.data.model.SocketType
import com.evgramacharge.app.data.model.UiState
import com.evgramacharge.app.presentation.viewmodel.AuthViewModel
import com.evgramacharge.app.presentation.viewmodel.MapViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    viewModel: MapViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val locationPermissionState = rememberPermissionState(Manifest.permission.ACCESS_FINE_LOCATION)
    val uiState by viewModel.uiState.collectAsState()
    val bookingState by viewModel.bookingState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()
    
    val currentSearchQuery by viewModel.searchQuery.collectAsState()
    val currentSocketFilter by viewModel.socketFilter.collectAsState()
    val currentMaxPriceFilter by viewModel.maxPriceFilter.collectAsState()

    var selectedPoint by remember { mutableStateOf<ChargingPoint?>(null) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var mapView by remember { mutableStateOf<MapView?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        if (!locationPermissionState.status.isGranted) {
            locationPermissionState.launchPermissionRequest()
        }
        // Initialize osmdroid
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context))
        Configuration.getInstance().userAgentValue = context.packageName
    }

    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                MapView(ctx).apply {
                    setTileSource(TileSourceFactory.MAPNIK)
                    setMultiTouchControls(true)
                    controller.setZoom(13.0)
                    controller.setCenter(GeoPoint(12.9716, 77.5946)) // Bengaluru center
                    
                    val myLocationOverlay = org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay(org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider(ctx), this)
                    myLocationOverlay.enableMyLocation()
                    this.overlays.add(myLocationOverlay)
                    
                    mapView = this
                }
            },
            update = { view ->
                val locationOverlay = view.overlays.filterIsInstance<org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay>().firstOrNull()
                view.overlays.clear()
                locationOverlay?.let { view.overlays.add(it) }
                
                if (uiState is UiState.Success) {
                    val points = (uiState as UiState.Success<List<ChargingPoint>>).data
                    points.forEach { point ->
                        val marker = Marker(view)
                        marker.position = GeoPoint(point.lat, point.lng)
                        marker.title = point.name
                        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                        
                        // Create a custom bitmap for the marker to make it look professional
                        val width = 100
                        val height = 120
                        val bitmap = android.graphics.Bitmap.createBitmap(width, height, android.graphics.Bitmap.Config.ARGB_8888)
                        val canvas = android.graphics.Canvas(bitmap)
                        val paint = android.graphics.Paint().apply {
                            isAntiAlias = true
                            style = android.graphics.Paint.Style.FILL
                        }

                        // Draw background
                        paint.color = if (point.isAvailable) android.graphics.Color.parseColor("#10B981") else android.graphics.Color.parseColor("#EF4444")
                        canvas.drawRoundRect(android.graphics.RectF(0f, 0f, 100f, 80f), 20f, 20f, paint)

                        // Draw bottom triangle
                        val path = android.graphics.Path()
                        path.moveTo(35f, 80f)
                        path.lineTo(65f, 80f)
                        path.lineTo(50f, 110f)
                        path.close()
                        canvas.drawPath(path, paint)

                        // Draw text
                        val textPaint = android.graphics.Paint().apply {
                            color = android.graphics.Color.WHITE
                            textSize = 36f
                            isAntiAlias = true
                            textAlign = android.graphics.Paint.Align.CENTER
                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                        }
                        val text = if(point.socketType == SocketType.FIVE_A) "5A" else "15A"
                        // yPos formula to center text vertically in the 80 height box
                        val yPos = 40f - (textPaint.descent() + textPaint.ascent()) / 2f
                        canvas.drawText(text, 50f, yPos, textPaint)
                        
                        marker.icon = android.graphics.drawable.BitmapDrawable(view.resources, bitmap)

                        marker.setOnMarkerClickListener { m, v ->
                            v.controller.animateTo(m.position)
                            selectedPoint = point
                            showBottomSheet = true
                            true
                        }
                        view.overlays.add(marker)
                    }
                    view.invalidate()
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Top Search and Filters bar
        Column(modifier = Modifier.align(Alignment.TopCenter).padding(horizontal = 16.dp, vertical = 24.dp).fillMaxWidth()) {
            Card(
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth()
            ) {
                TextField(
                    value = currentSearchQuery,
                    onValueChange = { viewModel.setSearchQuery(it) },
                    placeholder = { Text("Search places...", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search icon", tint = MaterialTheme.colorScheme.primary) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    singleLine = true
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                FilterChip(
                    selected = currentSocketFilter == SocketType.FIVE_A,
                    onClick = { viewModel.setSocketFilter(if (currentSocketFilter == SocketType.FIVE_A) null else SocketType.FIVE_A) },
                    label = { Text("5A Socket", fontWeight = FontWeight.SemiBold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    elevation = FilterChipDefaults.filterChipElevation(elevation = 8.dp),
                    border = null,
                    shape = RoundedCornerShape(16.dp)
                )
                FilterChip(
                    selected = currentSocketFilter == SocketType.FIFTEEN_A,
                    onClick = { viewModel.setSocketFilter(if (currentSocketFilter == SocketType.FIFTEEN_A) null else SocketType.FIFTEEN_A) },
                    label = { Text("15A Socket", fontWeight = FontWeight.SemiBold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    elevation = FilterChipDefaults.filterChipElevation(elevation = 8.dp),
                    border = null,
                    shape = RoundedCornerShape(16.dp)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Max Price", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = "₹${currentMaxPriceFilter ?: 100}/hr", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    }
                    Slider(
                        value = currentMaxPriceFilter?.toFloat() ?: 100f,
                        onValueChange = { viewModel.setMaxPrice(it.toInt()) },
                        valueRange = 10f..100f,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary,
                            inactiveTrackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    )
                }
            }
            if (uiState is UiState.Success && (uiState as UiState.Success<List<ChargingPoint>>).data.isEmpty() && currentSearchQuery.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "This Kirana store is not signed up with us yet.",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        if (uiState is UiState.Loading) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(150.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center).size(60.dp),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
        
        if (uiState is UiState.Error) {
            val err = (uiState as UiState.Error).message
            Card(
                modifier = Modifier.align(Alignment.Center).padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(err, color = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.padding(16.dp))
            }
        }
        
        FloatingActionButton(
            onClick = {
                val locOverlay = mapView?.overlays?.filterIsInstance<org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay>()?.firstOrNull()
                if (locOverlay != null && locOverlay.myLocation != null) {
                    mapView?.controller?.animateTo(locOverlay.myLocation)
                    mapView?.controller?.setZoom(16.0)
                }
            },
            modifier = Modifier.align(Alignment.BottomEnd).padding(32.dp),
            containerColor = MaterialTheme.colorScheme.primary
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = "My Location", tint = MaterialTheme.colorScheme.onPrimary)
        }

        if (selectedPoint != null && showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                    selectedPoint = null
                },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 16.dp).verticalScroll(rememberScrollState())) {
                    Text(selectedPoint!!.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                    if (selectedPoint!!.reviewCount > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = "Star", tint = Color(0xFFFFC107), modifier = Modifier.size(20.dp))
                            Text(" ${String.format(java.util.Locale.getDefault(), "%.1f", selectedPoint!!.rating)} (${selectedPoint!!.reviewCount} reviews)", style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                    if (selectedPoint!!.imageUrl != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        coil.compose.AsyncImage(
                            model = selectedPoint!!.imageUrl,
                            contentDescription = "Point Image",
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                            modifier = Modifier.fillMaxWidth().height(150.dp).clip(RoundedCornerShape(12.dp))
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Socket Type:", fontWeight = FontWeight.SemiBold)
                                Text(if (selectedPoint!!.socketType == SocketType.FIVE_A) "5A Socket" else "15A Socket")
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Price:", fontWeight = FontWeight.SemiBold)
                                Text("₹${selectedPoint!!.pricePerHour}/hr", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Estimated Cost (1h):", fontWeight = FontWeight.SemiBold)
                                Text("₹${selectedPoint!!.pricePerHour}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Operating Hours:", fontWeight = FontWeight.SemiBold)
                                Text(selectedPoint!!.operatingHours)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Text("Recent Feedback", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    val hash = selectedPoint!!.id.hashCode()
                    val feedbackData = if (hash % 2 == 0) {
                        listOf(
                            Pair("Ramesh K.", "Very helpful host. The socket was easily accessible."),
                            Pair("Sunitha Reddy", "Charged my scooter while picking up groceries. Convenient!")
                        )
                    } else {
                        listOf(
                            Pair("Aditya V.", "Good voltage, consistent charging. Highly recommend."),
                            Pair("Meghan C.", "The shop owner offered me tea while I waited for 20 mins!")
                        )
                    }

                    feedbackData.forEach { (name, comment) ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AccountCircle, contentDescription = null, modifier = Modifier.size(24.dp), tint = MaterialTheme.colorScheme.primary)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(comment, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f))
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = { 
                            showConfirmationDialog = true
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = selectedPoint!!.isAvailable,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (selectedPoint!!.isAvailable) "Book Now" else "Currently Busy", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
        
        if (showConfirmationDialog && selectedPoint != null) {
            AlertDialog(
                onDismissRequest = { showConfirmationDialog = false },
                title = { Text("Confirm Booking") },
                text = { 
                    Column {
                        Text("You are about to book:")
                        Text("${selectedPoint!!.name}", fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Estimated Cost: ₹${selectedPoint!!.pricePerHour} for 1 hour")
                        if (bookingState is UiState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.padding(top = 16.dp).size(40.dp))
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { 
                            currentUser?.uid?.let { uid ->
                                viewModel.bookPoint(selectedPoint!!, uid) 
                            }
                        },
                        enabled = bookingState !is UiState.Loading
                    ) {
                        Text("Confirm")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmationDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        when (bookingState) {
            is UiState.Success -> {
                AlertDialog(
                    onDismissRequest = { 
                        viewModel.resetBookingState()
                        showConfirmationDialog = false
                    },
                    title = { Text("Booking Confirmed") },
                    text = { 
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Booking Confirmed",
                                tint = Color(0xFF00C853),
                                modifier = Modifier.size(100.dp)
                            )
                            Text("Your booking has been successfully created!") 
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { 
                            viewModel.resetBookingState()
                            showConfirmationDialog = false
                            showBottomSheet = false
                            selectedPoint = null 
                        }) {
                            Text("OK")
                        }
                    }
                )
            }
            is UiState.Error -> {
                AlertDialog(
                    onDismissRequest = { viewModel.resetBookingState() },
                    title = { Text("Booking Failed") },
                    text = { Text((bookingState as UiState.Error).message) },
                    confirmButton = {
                        TextButton(onClick = { viewModel.resetBookingState() }) {
                            Text("OK")
                        }
                    }
                )
            }
            else -> {}
        }
    }
}
