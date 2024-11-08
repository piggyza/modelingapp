package com.example.modelbookingapp.ui.screens.model

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.modelbookingapp.data.model.ModelProfile
import com.example.modelbookingapp.data.model.Resource
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ModelProfileViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : ViewModel() {

    private val _profileState = MutableStateFlow<Resource<ModelProfile>>(Resource.Loading)
    val profileState: StateFlow<Resource<ModelProfile>> = _profileState

    private val _uploadState = MutableStateFlow<Resource<String>>(Resource.Success(""))
    val uploadState: StateFlow<Resource<String>> = _uploadState

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: throw Exception("User not found")
                val profile = firestore.collection("models")
                    .document(userId)
                    .get()
                    .await()
                    .toObject(ModelProfile::class.java)
                    ?: ModelProfile(id = userId)

                _profileState.value = Resource.Success(profile)
            } catch (e: Exception) {
                _profileState.value = Resource.Error(e.message ?: "Failed to load profile")
            }
        }
    }

    fun updateProfile(profile: ModelProfile) {
        viewModelScope.launch {
            try {
                val userId = auth.currentUser?.uid ?: throw Exception("User not found")
                firestore.collection("models")
                    .document(userId)
                    .set(profile)
                    .await()

                _profileState.value = Resource.Success(profile)
            } catch (e: Exception) {
                _profileState.value = Resource.Error(e.message ?: "Failed to update profile")
            }
        }
    }

    fun uploadPhoto(uri: Uri) {
        viewModelScope.launch {
            try {
                _uploadState.value = Resource.Loading

                val userId = auth.currentUser?.uid ?: throw Exception("User not found")
                val fileName = "models/$userId/${UUID.randomUUID()}"
                val photoRef = storage.reference.child(fileName)

                photoRef.putFile(uri).await()
                val downloadUrl = photoRef.downloadUrl.await().toString()

                // Update profile with new photo
                val currentProfile = (_profileState.value as? Resource.Success)?.data
                    ?: throw Exception("Profile not loaded")

                val updatedProfile = currentProfile.copy(
                    photos = currentProfile.photos + downloadUrl
                )

                updateProfile(updatedProfile)
                _uploadState.value = Resource.Success(downloadUrl)
            } catch (e: Exception) {
                _uploadState.value = Resource.Error(e.message ?: "Failed to upload photo")
            }
        }
    }

    fun deletePhoto(photoUrl: String) {
        viewModelScope.launch {
            try {
                val currentProfile = (_profileState.value as? Resource.Success)?.data
                    ?: throw Exception("Profile not loaded")

                val updatedPhotos = currentProfile.photos.filter { it != photoUrl }
                val updatedProfile = currentProfile.copy(photos = updatedPhotos)

                updateProfile(updatedProfile)

                // Delete from storage
                storage.getReferenceFromUrl(photoUrl).delete().await()
            } catch (e: Exception) {
                _profileState.value = Resource.Error(e.message ?: "Failed to delete photo")
            }
        }
    }
}