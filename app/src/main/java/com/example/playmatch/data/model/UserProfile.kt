package com.example.playmatch.data.model

data class UserProfile(
    val userId: String = "",
    val age: Int = 0,
    val bio: String = "",
    val interests: List<String> = emptyList(),
    val photos: List<String> = emptyList(),
    val location: String = "",
    val gender: String = "",
    val lookingFor: String = "",
    val height: String = "", // Estatura en cm
    val musicTaste: List<String> = emptyList(), // Gustos musicales
    val preferredGender: String = "",
    val profilePhotoUrl: String = "",
    val coverPhotoUrl: String = "",
    val createdAt: Long = System.currentTimeMillis()
)



