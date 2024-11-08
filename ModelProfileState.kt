package com.example.modelbookingapp.data.model

data class ModelProfileState(
    val profile: Resource<ModelProfile> = Resource.Loading,
    val isUpdating: Boolean = false,
    val isUploadingPhotos: Boolean = false,
    val error: String? = null
)