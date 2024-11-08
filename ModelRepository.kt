package com.example.modelbookingapp.data.repository

import com.example.modelbookingapp.data.model.ModelProfile
import com.example.modelbookingapp.data.model.Resource
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ModelRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun getProfile(modelId: String): Flow<Resource<ModelProfile>> = flow {
        try {
            emit(Resource.Loading)
            val profile = firestore.collection("models")
                .document(modelId)
                .get()
                .await()
                .toObject(ModelProfile::class.java)
                ?.copy(id = modelId)
                ?: throw Exception("Profile not found")
            emit(Resource.Success(profile))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to load profile"))
        }
    }

    fun updateProfile(modelId: String, profile: ModelProfile): Flow<Resource<Unit>> = flow {
        try {
            emit(Resource.Loading)
            firestore.collection("models")
                .document(modelId)
                .set(profile)
                .await()
            emit(Resource.Success(Unit))
        } catch (e: Exception) {
            emit(Resource.Error(e.message ?: "Failed to update profile"))
        }
    }
}