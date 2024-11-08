package com.example.modelbookingapp.data.repository

import com.example.modelbookingapp.data.model.User
import com.example.modelbookingapp.data.model.UserType
import com.example.modelbookingapp.utils.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {
    suspend fun signIn(email: String, password: String): Resource<User> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("Authentication failed")

            val userDoc = firestore.collection("users").document(user.uid).get().await()
            if (!userDoc.exists()) {
                throw Exception("User data not found")
            }

            val userType = userDoc.getString("userType")?.let { UserType.valueOf(it) }
                ?: throw Exception("Invalid user type")

            val userData = userDoc.data
            Resource.Success(User(
                id = user.uid,
                email = user.email ?: "",
                userType = userType,
                name = userData?.get("name") as? String ?: "",
                profileCompleted = userData?.get("profileCompleted") as? Boolean ?: false,
                createdAt = userData?.get("createdAt") as? Long ?: System.currentTimeMillis()
            ))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun signUp(
        email: String,
        password: String,
        name: String,
        userType: UserType
    ): Resource<User> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user ?: throw Exception("User creation failed")

            val userData = hashMapOf(
                "email" to email,
                "name" to name,
                "userType" to userType.name,
                "profileCompleted" to false,
                "createdAt" to System.currentTimeMillis()
            )

            firestore.collection("users").document(user.uid)
                .set(userData)
                .await()

            Resource.Success(User(
                id = user.uid,
                email = email,
                name = name,
                userType = userType,
                profileCompleted = false,
                createdAt = System.currentTimeMillis()
            ))
        } catch (e: Exception) {
            Resource.Error(e.message ?: "An unknown error occurred")
        }
    }

    suspend fun updateProfile(userId: String, updates: Map<String, Any>): Resource<Unit> {
        return try {
            firestore.collection("users").document(userId)
                .update(updates)
                .await()
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Failed to update profile")
        }
    }

    fun signOut() {
        auth.signOut()
    }

    fun getCurrentUser(): User? {
        val firebaseUser = auth.currentUser ?: return null
        return User(
            id = firebaseUser.uid,
            email = firebaseUser.email ?: "",
            userType = null  // Will be fetched when needed
        )
    }

    suspend fun getCurrentUserType(): UserType? {
        val user = auth.currentUser ?: return null
        return try {
            val userDoc = firestore.collection("users").document(user.uid).get().await()
            userDoc.getString("userType")?.let { UserType.valueOf(it) }
        } catch (e: Exception) {
            null
        }
    }
}