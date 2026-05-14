package com.evgramacharge.app.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.evgramacharge.app.presentation.ui.map.MapScreen
import com.evgramacharge.app.presentation.ui.calculator.CalculatorScreen
import com.evgramacharge.app.presentation.ui.host.ProfileScreen
import com.evgramacharge.app.presentation.viewmodel.AuthViewModel
import com.evgramacharge.app.presentation.viewmodel.MainViewModel
import kotlinx.coroutines.flow.collectLatest

@Composable
fun MainScreen(
    authViewModel: AuthViewModel = hiltViewModel(),
    mainViewModel: MainViewModel = hiltViewModel()
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Map", "Calculator", "Profile")
    val icons = listOf(Icons.Default.LocationOn, Icons.Default.Info, Icons.Default.Person)
    
    val currentUser by authViewModel.currentUser.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(currentUser) {
        currentUser?.let {
            mainViewModel.listenForBookings(it.uid, it.isHost)
        }
    }

    LaunchedEffect(Unit) {
        mainViewModel.notifications.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        bottomBar = {
            NavigationBar {
                tabs.forEachIndexed { index, title ->
                    NavigationBarItem(
                        icon = { Icon(icons[index], contentDescription = title) },
                        label = { Text(title) },
                        selected = selectedTab == index,
                        onClick = { selectedTab = index }
                    )
                }
            }
        }
    ) { paddingValues ->
        val modifier = Modifier.padding(paddingValues)
        AnimatedContent(
            targetState = selectedTab,
            transitionSpec = {
                if (targetState > initialState) {
                    slideInHorizontally(animationSpec = tween(300)) { it } + fadeIn() togetherWith slideOutHorizontally(animationSpec = tween(300)) { -it } + fadeOut()
                } else {
                    slideInHorizontally(animationSpec = tween(300)) { -it } + fadeIn() togetherWith slideOutHorizontally(animationSpec = tween(300)) { it } + fadeOut()
                }
            },
            label = "Tab Animation"
        ) { targetTab ->
            when (targetTab) {
                0 -> MapScreen(modifier)
                1 -> CalculatorScreen(modifier)
                2 -> ProfileScreen(modifier)
            }
        }
    }
}
