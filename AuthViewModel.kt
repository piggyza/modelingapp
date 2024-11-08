package com.example.modelbookingapp.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.modelbookingapp.data.model.Resource
import com.example.modelbookingapp.data.model.UserType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    private val _authState = MutableStateFlow<Resource<UserType>>(Resource.Success(UserType.CLIENT))
    val authState: StateFlow<Resource<UserType>> = _authState

    fun signIn(email: String, password: String) {
        viewModelScope.launch {
            try {
                _authState.value = Resource.Loading
                auth.signInWithEmailAndPassword(email, password).await()
                val userType = getUserType(auth.currentUser?.uid)
                _authState.value = Resource.Success(userType)
            } catch (e: Exception) {
                _authState.value = Resource.Error(e.message ?: "Authentication failed")
            }
        }
    }

    fun signUp(email: String, password: String, userType: UserType) {
        viewModelScope.launch {
            try {
                _authState.value = Resource.Loading
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                result.user?.uid?.let { uid ->
                    createUserProfile(uid, userType)
                    _authState.value = Resource.Success(userType)
                }
            } catch (e: Exception) {
                _authState.value = Resource.Error(e.message ?: "Registration failed")
            }
        }
    }

    fun signOut() {
        auth.signOut()
    }

    private suspend fun getUserType(userId: String?): UserType {
        if (userId == null) throw Exception("User not found")
        return try {
            val doc = firestore.collection("users").document(userId).get().await()
            doc.getString("type")?.let { UserType.valueOf(it) } ?: UserType.CLIENT
        } catch (e: Exception) {
            throw Exception("Failed to get user type")
        }
    }

    private suspend fun createUserProfile(userId: String, userType: UserType) {
        firestore.collection("users").document(userId)
            .set(mapOf(
                "type" to userType.name,
                "createdAt" to System.currentTimeMillis()
            )).await()
    }
}