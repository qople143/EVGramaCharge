package com.evgramacharge.app.presentation.viewmodel

import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.evgramacharge.app.data.model.User
import com.evgramacharge.app.data.repository.FirebaseRepository
import com.google.firebase.auth.ActionCodeSettings
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: FirebaseRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _linkSent = MutableStateFlow(false)
    val linkSent: StateFlow<Boolean> = _linkSent.asStateFlow()

    private val prefs: SharedPreferences = context.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    init {
        auth.currentUser?.let { firebaseUser ->
            fetchUserProfile(firebaseUser.uid)
        }
    }

    fun sendEmailLink(email: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            _linkSent.value = false
            try {
                val actionCodeSettings = ActionCodeSettings.newBuilder()
                    .setUrl("https://ev-grama-charge-mock.firebaseapp.com/finishSignUp")
                    .setHandleCodeInApp(true)
                    .setAndroidPackageName("com.evgramacharge.app", true, "1")
                    .build()

                auth.sendSignInLinkToEmail(email, actionCodeSettings)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            prefs.edit().putString("emailForSignIn", email).apply()
                            _linkSent.value = true
                        } else {
                            _error.value = task.exception?.message ?: "Failed to send verification link. Note: For email link auth, the domain must be authorized in Firebase Console."
                        }
                        _isLoading.value = false
                    }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun handleIntentData(intentDataString: String?, nameIfNew: String = "", isHostIfNew: Boolean = false) {
        if (intentDataString != null && auth.isSignInWithEmailLink(intentDataString)) {
            val email = prefs.getString("emailForSignIn", null)
            if (email != null) {
                signInWithEmailLink(email, intentDataString, nameIfNew, isHostIfNew)
            } else {
                _error.value = "Email not found locally. Please enter it to complete sign in."
            }
        }
    }

    fun signInWithEmailLink(email: String, emailLink: String, nameIfNew: String = "User", isHostIfNew: Boolean = false) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                auth.signInWithEmailLink(email, emailLink)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val result = task.result
                            val isNewUser = result?.additionalUserInfo?.isNewUser ?: false
                            if (isNewUser) {
                                result?.user?.let {
                                    val user = User(uid = it.uid, name = nameIfNew.ifEmpty { "User" }, email = email, isHost = isHostIfNew)
                                    saveUserProfile(user)
                                }
                            } else {
                                result?.user?.let { fetchUserProfile(it.uid) }
                            }
                        } else {
                            _error.value = task.exception?.message ?: "Failed to sign in with link"
                            _isLoading.value = false
                        }
                    }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result.user?.let { fetchUserProfile(it.uid) }
                    } else {
                        _error.value = task.exception?.message ?: "Login failed"
                        _isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun signup(email: String, pass: String, name: String, isHost: Boolean) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result.user?.let {
                            val user = User(uid = it.uid, name = name, email = email, isHost = isHost)
                            saveUserProfile(user)
                        }
                    } else {
                        _error.value = task.exception?.message ?: "Signup failed"
                        _isLoading.value = false
                    }
                }
            } catch (e: Exception) {
                _error.value = e.message
                _isLoading.value = false
            }
        }
    }

    fun signout() {
        auth.signOut()
        _currentUser.value = null
    }

    private fun fetchUserProfile(uid: String) {
        viewModelScope.launch {
            val user = repository.getUserProfile(uid)
            _currentUser.value = user
            _isLoading.value = false
        }
    }

    fun saveUserProfile(user: User) {
        viewModelScope.launch {
            repository.saveUserProfile(user)
            _currentUser.value = user
            _isLoading.value = false
        }
    }

    fun updateProfilePicture(url: String) {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            val updatedUser = user.copy(profilePictureUrl = url)
            repository.saveUserProfile(updatedUser)
            _currentUser.value = updatedUser
        }
    }

    fun updateUserProfileData(user: User) {
        viewModelScope.launch {
            repository.saveUserProfile(user)
            _currentUser.value = user
        }
    }
}
