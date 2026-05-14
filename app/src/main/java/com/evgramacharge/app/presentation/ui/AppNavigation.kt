package com.evgramacharge.app.presentation.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.evgramacharge.app.presentation.ui.auth.LoginScreen
import com.evgramacharge.app.presentation.ui.onboarding.OnboardingScreen
import com.evgramacharge.app.presentation.viewmodel.AuthViewModel
import com.evgramacharge.app.presentation.viewmodel.SettingsViewModel

@Composable
fun AppNavigation(
    authViewModel: AuthViewModel = hiltViewModel(),
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    val currentUser by authViewModel.currentUser.collectAsState()
    val onboardingCompleted by settingsViewModel.onboardingCompleted.collectAsState()
    
    val currentScreen = when {
        !onboardingCompleted -> "ONBOARDING"
        currentUser == null -> "LOGIN"
        else -> "MAIN"
    }

    AnimatedContent(
        targetState = currentScreen,
        transitionSpec = {
            if (targetState == "MAIN" && initialState == "LOGIN") {
                slideInHorizontally(animationSpec = tween(500)) { it } + fadeIn() togetherWith slideOutHorizontally(animationSpec = tween(500)) { -it } + fadeOut()
            } else if (targetState == "LOGIN" && initialState == "ONBOARDING") {
                slideInHorizontally(animationSpec = tween(500)) { it } + fadeIn() togetherWith slideOutHorizontally(animationSpec = tween(500)) { -it } + fadeOut()
            } else {
                fadeIn(animationSpec = tween(500)) togetherWith fadeOut(animationSpec = tween(500))
            }
        },
        label = "App Navigation"
    ) { screen ->
        when (screen) {
            "ONBOARDING" -> OnboardingScreen(onFinish = { settingsViewModel.completeOnboarding() })
            "LOGIN" -> LoginScreen(onLoginSuccess = { })
            "MAIN" -> MainScreen()
        }
    }
}
