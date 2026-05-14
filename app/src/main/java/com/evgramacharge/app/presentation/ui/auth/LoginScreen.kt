package com.evgramacharge.app.presentation.ui.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.evgramacharge.app.presentation.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val sharedPrefs = remember { context.getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE) }
    
    var email by remember { mutableStateOf(sharedPrefs.getString("saved_email", "") ?: "") }
    var password by remember { mutableStateOf(sharedPrefs.getString("saved_password", "") ?: "") }
    var rememberMe by remember { mutableStateOf(sharedPrefs.getBoolean("remember_me", false)) }
    
    var name by remember { mutableStateOf("") }
    var isSignup by remember { mutableStateOf(false) }
    var isPasswordless by remember { mutableStateOf(false) }
    var isHostUser by remember { mutableStateOf(false) }
    
    val currentUser by viewModel.currentUser.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val linkSent by viewModel.linkSent.collectAsState()

    val lottieComposition by rememberLottieComposition(
        spec = LottieCompositionSpec.Url("https://lottie.host/80c2f8d8-7953-48b2-bdd0-87a2a09cb0e1/uDrdZJqXZc.json")
    )
    val progress by animateLottieCompositionAsState(
        composition = lottieComposition,
        iterations = LottieConstants.IterateForever
    )

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            onLoginSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .padding(bottom = 16.dp)
        ) {
            LottieAnimation(
                composition = lottieComposition,
                progress = { progress },
                modifier = Modifier.fillMaxSize()
            )
        }

        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (isPasswordless) "Passwordless Sign-in" else if (isSignup) "Create Account" else "Welcome Back",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                if (!isPasswordless && isSignup) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Name") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Register as:", fontWeight = FontWeight.Medium)
                        Spacer(modifier = Modifier.width(8.dp))
                        RadioButton(
                            selected = !isHostUser,
                            onClick = { isHostUser = false }
                        )
                        Text("EV Rider")
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(
                            selected = isHostUser,
                            onClick = { isHostUser = true }
                        )
                        Text("Kirana Host")
                    }
                }

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
                )

                if (!isPasswordless) {
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
                    )
                }

                if (!isPasswordless) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = rememberMe,
                            onCheckedChange = { rememberMe = it }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Remember Me", style = MaterialTheme.typography.bodyMedium)
                    }
                }

                if (error != null) {
                    Text(
                        text = error ?: "An error occurred",
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                if (linkSent) {
                    Text(
                        text = "Sign-in link sent to your email!",
                        color = Color(0xFF00C853),
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                Button(
                    onClick = {
                        if (rememberMe) {
                            sharedPrefs.edit()
                                .putString("saved_email", email)
                                .putString("saved_password", password)
                                .putBoolean("remember_me", true)
                                .apply()
                        } else {
                            sharedPrefs.edit()
                                .remove("saved_email")
                                .remove("saved_password")
                                .putBoolean("remember_me", false)
                                .apply()
                        }
                        
                        if (isPasswordless) {
                            viewModel.sendEmailLink(email)
                        } else if (isSignup) {
                            viewModel.signup(email, password, name, isHostUser)
                        } else {
                            viewModel.login(email, password)
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(28.dp),
                    enabled = !isLoading && email.isNotBlank() && (isPasswordless || password.isNotBlank())
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(24.dp))
                    } else {
                        Text(if (isPasswordless) "Send Magic Link" else if (isSignup) "Sign Up" else "Login", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    }
                }

                if (!isPasswordless) {
                    TextButton(
                        onClick = { isSignup = !isSignup },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text(if (isSignup) "Already have an account? Login" else "Don't have an account? Sign up")
                    }
                }

                TextButton(
                    onClick = { isPasswordless = !isPasswordless },
                ) {
                    Text(if (isPasswordless) "Use password instead" else "Sign in with Email Link (Passwordless)")
                }
            }
        }
    }
}
