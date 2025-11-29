package com.example.playmatch.data.repository

import com.example.playmatch.data.model.UserProfile
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class ProfileRepository {
    private val db = FirebaseFirestore.getInstance()
    
    suspend fun saveProfile(profile: UserProfile): Result<Unit> {
        return try {
            db.collection("profiles").document(profile.userId).set(profile).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getProfile(userId: String): UserProfile? {
        return try {
            val document = db.collection("profiles").document(userId).get().await()
            document.toObject(UserProfile::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun getAllProfiles(excludeUserId: String): List<UserProfile> {
        return try {
            val snapshot = db.collection("profiles")
                .whereNotEqualTo("userId", excludeUserId)
                .get()
                .await()
            snapshot.documents.mapNotNull { it.toObject(UserProfile::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateProfilePhotos(
        userId: String,
        profilePhotoUrl: String? = null,
        coverPhotoUrl: String? = null
    ): Result<UserProfile> {
        return try {
            val current = getProfile(userId) ?: UserProfile(userId = userId)
            val updated = current.copy(
                profilePhotoUrl = profilePhotoUrl ?: current.profilePhotoUrl,
                coverPhotoUrl = coverPhotoUrl ?: current.coverPhotoUrl
            )
            db.collection("profiles").document(userId)
                .set(updated, SetOptions.merge())
                .await()
            Result.success(updated)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}



