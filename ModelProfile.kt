package com.example.modelbookingapp.data.model

data class ModelProfile(
    val id: String = "",
    val name: String = "",
    val bio: String = "",
    val photos: List<String> = emptyList(),
    val hourlyRate: Double = 0.0,
    val availability: List<String> = emptyList(),
    val specialties: List<String> = emptyList(),
    val rating: Double = 0.0,
    val reviewCount: Int = 0
) {
    fun copy(photos: List<String>): ModelProfile {
        return ModelProfile(
            id = this.id,
            name = this.name,
            bio = this.bio,
            photos = photos,
            hourlyRate = this.hourlyRate,
            availability = this.availability,
            specialties = this.specialties,
            rating = this.rating,
            reviewCount = this.reviewCount
        )
    }
}