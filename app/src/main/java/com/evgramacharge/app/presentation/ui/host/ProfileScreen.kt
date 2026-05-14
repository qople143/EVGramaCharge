package com.evgramacharge.app.presentation.ui.host

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import androidx.compose.material3.*
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.evgramacharge.app.data.model.BookingStatus
import com.evgramacharge.app.data.model.ChargingPoint
import com.evgramacharge.app.presentation.viewmodel.AuthViewModel
import com.evgramacharge.app.presentation.viewmodel.ProfileViewModel
import com.evgramacharge.app.presentation.viewmodel.SettingsViewModel
import java.util.Date
import com.evgramacharge.app.data.model.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val hostPoints by profileViewModel.hostPoints.collectAsState()
    val hostBookings by profileViewModel.hostBookings.collectAsState()
    val userBookings by profileViewModel.userBookings.collectAsState()
    val darkModeEnabled by settingsViewModel.darkModeEnabled.collectAsState()

    var showManagePointsDialog by remember { mutableStateOf(false) }
    var showUserBookingsDialog by remember { mutableStateOf(false) }
    var showFaqDialog by remember { mutableStateOf(false) }
    var picUrlInput by remember { mutableStateOf("") }
    var showPicDialog by remember { mutableStateOf(false) }

    var showEditProfileDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editEmail by remember { mutableStateOf("") }
    var editPreferences by remember { mutableStateOf("") }

    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            if (user.isHost) {
                profileViewModel.loadHostData(user.uid)
            } else {
                profileViewModel.loadUserData(user.uid)
            }
            editName = user.name
            editEmail = user.email
            editPreferences = user.preferences ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Profile", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground
                )
            )
        }
    ) { paddingVals ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingVals)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            currentUser?.let { user ->
                // Avatar
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .padding(bottom = 16.dp)
                ) {
                    if (!user.profilePictureUrl.isNullOrBlank()) {
                        AsyncImage(
                            model = user.profilePictureUrl,
                            contentDescription = "Profile Picture",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(androidx.compose.foundation.shape.CircleShape)
                        )
                    } else {
                        val avatarUrl = if (user.isHost) {
                            "https://ui-avatars.com/api/?name=${java.net.URLEncoder.encode(user.name, "UTF-8")}&background=0D8ABC&color=fff&size=200"
                        } else {
                            "https://ui-avatars.com/api/?name=${java.net.URLEncoder.encode(user.name, "UTF-8")}&background=random&color=fff&size=200"
                        }
                        AsyncImage(
                            model = avatarUrl,
                            contentDescription = "Default Avatar",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(androidx.compose.foundation.shape.CircleShape)
                        )
                    }
                    SmallFloatingActionButton(
                        onClick = { showPicDialog = true },
                        modifier = Modifier.align(Alignment.BottomEnd)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Edit Picture")
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Hello, ${user.name}!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { showEditProfileDialog = true }) {
                        Icon(imageVector = Icons.Default.Info, contentDescription = "Edit Profile")
                    }
                }
                Text(
                    text = user.email,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                if (user.preferences.isNotEmpty()) {
                    Text(
                        text = "Food/Snack Preferences: ${user.preferences}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.height(16.dp))
                }

                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("App Settings", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
                        HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text("Dark Mode:")
                            Switch(
                                checked = darkModeEnabled ?: androidx.compose.foundation.isSystemInDarkTheme(),
                                onCheckedChange = { settingsViewModel.setDarkMode(it) }
                            )
                        }
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Account Details", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(bottom = 8.dp))
                        HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp))
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Account Type:")
                            Text(if (user.isHost) "Kirana Host" else "EV User", fontWeight = FontWeight.Bold)
                        }

                        if (!user.isHost) {
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showUserBookingsDialog = true },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("View My Bookings (${userBookings.size})")
                            }
                        }
                    }
                }

                if (user.isHost) {
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Kirana Host Dashboard", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.padding(bottom = 8.dp))
                            HorizontalDivider(modifier = Modifier.padding(bottom = 8.dp), color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                            
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Active Charging Points:", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text("${hostPoints.size}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Pending Bookings:", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text("${hostBookings.count { it.status == BookingStatus.REQUEST_SENT }}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }
                            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Total Bookings:", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onPrimaryContainer)
                                Text("${hostBookings.size}", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            }

                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { showManagePointsDialog = true },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text("Manage My Plug Points & Bookings")
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { showFaqDialog = true },
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                ) {
                    Icon(imageVector = Icons.Default.Info, contentDescription = "FAQ", modifier = Modifier.padding(end = 8.dp))
                    Text("Frequently Asked Questions (FAQ)")
                }

                OutlinedButton(
                    onClick = { authViewModel.signout() },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Sign Out", modifier = Modifier.padding(end = 8.dp))
                    Text("Sign Out")
                }
                Spacer(modifier = Modifier.height(24.dp))
            } ?: run {
                CircularProgressIndicator(modifier = Modifier.size(60.dp))
            }
        }
    }

    if (showPicDialog) {
        AlertDialog(
            onDismissRequest = { showPicDialog = false },
            title = { Text("Update Profile Picture") },
            text = {
                OutlinedTextField(
                    value = picUrlInput,
                    onValueChange = { picUrlInput = it },
                    label = { Text("Image URL") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = {
                    authViewModel.updateProfilePicture(picUrlInput)
                    showPicDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showPicDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showFaqDialog) {
        AlertDialog(
            onDismissRequest = { showFaqDialog = false },
            title = { Text("FAQ") },
            text = {
                LazyColumn {
                    item {
                        Text("Q: How to book a spot?", fontWeight = FontWeight.Bold)
                        Text("A: Use the Map to find a plug point, click it, and hit 'Book'. Wait for the host to accept.", modifier = Modifier.padding(bottom = 8.dp))
                        Text("Q: Can I cancel my booking?", fontWeight = FontWeight.Bold)
                        Text("A: Yes, open 'View My Bookings' in Profile and click 'Cancel'.", modifier = Modifier.padding(bottom = 8.dp))
                        Text("Q: How to host my plug point?", fontWeight = FontWeight.Bold)
                        Text("A: Register as a Kirana Host in the login screen. Or use settings to upgrade your account.", modifier = Modifier.padding(bottom = 8.dp))
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showFaqDialog = false }) { Text("Close") }
            }
        )
    }

    if (showEditProfileDialog) {
        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Edit Profile") },
            text = {
                Column {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Name") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = editEmail,
                        onValueChange = { editEmail = it },
                        label = { Text("Email") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                    OutlinedTextField(
                        value = editPreferences,
                        onValueChange = { editPreferences = it },
                        label = { Text("Snacks/Food Preferences") },
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    currentUser?.let { user ->
                        val updated = user.copy(name = editName, email = editEmail, preferences = editPreferences)
                        authViewModel.updateUserProfileData(updated)
                    }
                    showEditProfileDialog = false
                }) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showManagePointsDialog) {
        HostManagementModal(
            hostPoints = hostPoints,
            hostBookings = hostBookings,
            onDismiss = { showManagePointsDialog = false },
            onSavePoint = { profileViewModel.savePoint(it.copy(hostId = currentUser?.uid ?: "")) },
            onDeletePoint = { profileViewModel.deletePoint(it) },
            onUpdateBookingStatus = { id, status -> profileViewModel.updateBooking(id, status) }
        )
    }

    if (showUserBookingsDialog) {
        UserBookingsModal(
            userBookings = userBookings,
            onDismiss = { showUserBookingsDialog = false },
            onUpdateBookingStatus = { id, status -> profileViewModel.updateBooking(id, status) },
            onReviewBooking = { id, rating, review -> profileViewModel.reviewBooking(id, rating, review) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostManagementModal(
    hostPoints: List<ChargingPoint>,
    hostBookings: List<com.evgramacharge.app.data.model.Booking>,
    onDismiss: () -> Unit,
    onSavePoint: (ChargingPoint) -> Unit,
    onDeletePoint: (String) -> Unit,
    onUpdateBookingStatus: (String, BookingStatus) -> Unit
) {
    var showAddPoint by remember { mutableStateOf(false) }
    var sortDescending by remember { mutableStateOf(true) }
    var sortStatus by remember { mutableStateOf<BookingStatus?>(null) }
    
    val sortedFilteredBookings = remember(hostBookings, sortDescending, sortStatus) {
        val filtered = if (sortStatus != null) hostBookings.filter { it.status == sortStatus } else hostBookings
        if (sortDescending) filtered.sortedByDescending { it.timestamp }
        else filtered.sortedBy { it.timestamp }
    }

    if (showAddPoint) {
        AddEditPointDialog(
            point = null,
            onDismiss = { showAddPoint = false },
            onSave = { 
                onSavePoint(it)
                showAddPoint = false 
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f),
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        title = { Text("Host Management") },
        text = {
            LazyColumn {
                item {
                    Text("My Charging Points", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (hostPoints.isEmpty()) {
                    item {
                        Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Info, contentDescription = "Empty", modifier = Modifier.size(100.dp), tint = Color.Gray)
                            Text("No charging points added yet.", color = Color.Gray, modifier = Modifier.padding(top = 16.dp))
                        }
                    }
                }
                items(hostPoints) { point ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(point.name, fontWeight = FontWeight.Bold)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (point.reviewCount > 0) {
                                        Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                                        Text("${String.format(java.util.Locale.getDefault(), "%.1f", point.rating)} (${point.reviewCount}) | ", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Text("Price: ₹${point.pricePerHour}/hr | Availability: ${if (point.isAvailable) "Yes" else "Busy"}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                            IconButton(onClick = { onDeletePoint(point.id) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }
                }
                item {
                    TextButton(onClick = { showAddPoint = true }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                        Text("Add New Charging Point")
                    }
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    Text("Booking Requests", style = MaterialTheme.typography.titleMedium)
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        TextButton(onClick = { sortDescending = !sortDescending }) {
                            Text(if (sortDescending) "Newest First" else "Oldest First")
                        }
                        var expanded by remember { mutableStateOf(false) }
                        Box {
                            TextButton(onClick = { expanded = true }) {
                                Text(sortStatus?.name ?: "All Statuses")
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("All Statuses") },
                                    onClick = { sortStatus = null; expanded = false }
                                )
                                    BookingStatus.entries.forEach { status ->
                                    DropdownMenuItem(
                                        text = { Text(status.name) },
                                        onClick = { sortStatus = status; expanded = false }
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }
                if (sortedFilteredBookings.isEmpty()) {
                    item {
                        Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Info, contentDescription = "Empty", modifier = Modifier.size(100.dp), tint = Color.Gray)
                            Text("No booking requests found.", color = Color.Gray, modifier = Modifier.padding(top = 16.dp))
                        }
                    }
                }
                items(sortedFilteredBookings) { booking ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Point ID: ${booking.chargingPointId}", fontWeight = FontWeight.SemiBold)
                            Text("Status: ${booking.status.name}", color = if (booking.status == BookingStatus.ACCEPTED) Color(0xFF00C853) else if (booking.status == BookingStatus.CANCELLED) MaterialTheme.colorScheme.error else Color.Gray)
                            Text("Time: ${Date(booking.timestamp)}", style = MaterialTheme.typography.bodySmall)
                            
                            if (booking.status == BookingStatus.REQUEST_SENT || booking.status == BookingStatus.ACCEPTED) {
                                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                                    OutlinedButton(onClick = { onUpdateBookingStatus(booking.id, BookingStatus.CANCELLED) }) {
                                        Text("Cancel", color = MaterialTheme.colorScheme.error)
                                    }
                                    if (booking.status == BookingStatus.REQUEST_SENT) {
                                        Button(onClick = { onUpdateBookingStatus(booking.id, BookingStatus.ACCEPTED) }) {
                                            Text("Accept")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
fun AddEditPointDialog(point: ChargingPoint?, onDismiss: () -> Unit, onSave: (ChargingPoint) -> Unit) {
    var name by remember { mutableStateOf(point?.name ?: "") }
    var lat by remember { mutableStateOf(point?.lat?.toString() ?: "12.9352") }
    var lng by remember { mutableStateOf(point?.lng?.toString() ?: "77.5946") }
    var price by remember { mutableStateOf(point?.pricePerHour?.toString() ?: "0") }
    var hours by remember { mutableStateOf(point?.operatingHours ?: "9AM-9PM") }
    var imageUrl by remember { mutableStateOf(point?.imageUrl ?: "") }
    // ignoring socket for brevity
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (point == null) "Add Point" else "Edit Point") },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") })
                OutlinedTextField(value = lat, onValueChange = { lat = it }, label = { Text("Latitude") })
                OutlinedTextField(value = lng, onValueChange = { lng = it }, label = { Text("Longitude") })
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Price/Hr") })
                OutlinedTextField(value = hours, onValueChange = { hours = it }, label = { Text("Hours") })
                OutlinedTextField(value = imageUrl, onValueChange = { imageUrl = it }, label = { Text("Image URL") })
            }
        },
        confirmButton = {
            Button(onClick = { 
                onSave(ChargingPoint(
                    id = point?.id ?: "",
                    name = name,
                    lat = lat.toDoubleOrNull() ?: 0.0,
                    lng = lng.toDoubleOrNull() ?: 0.0,
                    pricePerHour = price.toIntOrNull() ?: 0,
                    operatingHours = hours,
                    imageUrl = imageUrl.ifBlank { null }
                )) 
            }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserBookingsModal(
    userBookings: List<com.evgramacharge.app.data.model.Booking>,
    onDismiss: () -> Unit,
    onUpdateBookingStatus: (String, BookingStatus) -> Unit,
    onReviewBooking: (String, Int, String) -> Unit
) {
    var sortDescending by remember { mutableStateOf(true) }
    var reviewBookingId by remember { mutableStateOf<String?>(null) }
    var rating by remember { androidx.compose.runtime.mutableIntStateOf(5) }
    var reviewText by remember { mutableStateOf("") }

    val sortedBookings = remember(userBookings, sortDescending) {
        if (sortDescending) userBookings.sortedByDescending { it.timestamp }
        else userBookings.sortedBy { it.timestamp }
    }

    if (reviewBookingId != null) {
        AlertDialog(
            onDismissRequest = { reviewBookingId = null },
            title = { Text("Rate & Review") },
            text = {
                Column {
                    Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                        for (i in 1..5) {
                            IconButton(onClick = { rating = i }) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Star $i",
                                    tint = if (i <= rating) Color(0xFFFFC107) else Color.Gray
                                )
                            }
                        }
                    }
                    OutlinedTextField(
                        value = reviewText,
                        onValueChange = { reviewText = it },
                        label = { Text("Review") },
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    onReviewBooking(reviewBookingId!!, rating, reviewText)
                    reviewBookingId = null
                }) { Text("Submit") }
            },
            dismissButton = {
                TextButton(onClick = { reviewBookingId = null }) { Text("Cancel") }
            }
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.fillMaxWidth().fillMaxHeight(0.8f),
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        title = {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("My Booking History")
                TextButton(onClick = { sortDescending = !sortDescending }) {
                    Text(if (sortDescending) "Newest First" else "Oldest First")
                }
            }
        },
        text = {
            LazyColumn {
                if (sortedBookings.isEmpty()) {
                    item {
                        Column(modifier = Modifier.fillMaxWidth().padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Info, contentDescription = "Empty", modifier = Modifier.size(100.dp), tint = Color.Gray)
                            Text("No bookings yet.", color = Color.Gray, modifier = Modifier.padding(top = 16.dp))
                        }
                    }
                }
                items(sortedBookings) { booking ->
                    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("Charging Point: ${booking.chargingPointId}", fontWeight = FontWeight.SemiBold)
                            Text("Status: ${booking.status.name}", color = if (booking.status == BookingStatus.ACCEPTED) Color(0xFF00C853) else if (booking.status == BookingStatus.CANCELLED) MaterialTheme.colorScheme.error else Color.Gray)
                            Text("Requested on: ${Date(booking.timestamp)}", style = MaterialTheme.typography.bodySmall)

                            if (booking.status == BookingStatus.REQUEST_SENT || booking.status == BookingStatus.ACCEPTED) {
                                Row(modifier = Modifier.fillMaxWidth().padding(top = 8.dp), horizontalArrangement = Arrangement.End) {
                                    OutlinedButton(onClick = { onUpdateBookingStatus(booking.id, BookingStatus.CANCELLED) }) {
                                        Text("Cancel Booking", color = MaterialTheme.colorScheme.error)
                                    }
                                    if (booking.status == BookingStatus.ACCEPTED) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Button(onClick = {
                                            rating = 5
                                            reviewText = ""
                                            reviewBookingId = booking.id
                                        }) {
                                            Text("Finish & Rate")
                                        }
                                    }
                                }
                            } else if (booking.status == BookingStatus.COMPLETED && booking.rating != null) {
                                Row(modifier = Modifier.padding(top = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, contentDescription = "Rating", tint = Color(0xFFFFC107), modifier = Modifier.size(16.dp))
                                    Text(" ${booking.rating} - ${booking.review}", style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 4.dp))
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}
