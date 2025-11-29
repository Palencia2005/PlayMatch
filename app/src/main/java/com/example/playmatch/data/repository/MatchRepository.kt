package com.example.playmatch.data.repository

import com.example.playmatch.data.model.Match
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MatchRepository {
    private val db = FirebaseFirestore.getInstance()
    
    suspend fun createMatch(match: Match): Result<String> {
        return try {
            db.collection("matches").document(match.id).set(match).await()
            Result.success(match.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserMatches(userId: String): List<Match> {
        return try {
            val snapshot1 = db.collection("matches")
                .whereEqualTo("userId1", userId)
                .get()
                .await()
            val snapshot2 = db.collection("matches")
                .whereEqualTo("userId2", userId)
                .get()
                .await()
            
            val matches1 = snapshot1.documents.mapNotNull { it.toObject(Match::class.java) }
            val matches2 = snapshot2.documents.mapNotNull { it.toObject(Match::class.java) }
            matches1 + matches2
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun checkMatchExists(userId1: String, userId2: String): Boolean {
        return try {
            val snapshot1 = db.collection("matches")
                .whereEqualTo("userId1", userId1)
                .whereEqualTo("userId2", userId2)
                .limit(1)
                .get()
                .await()
            val snapshot2 = db.collection("matches")
                .whereEqualTo("userId1", userId2)
                .whereEqualTo("userId2", userId1)
                .limit(1)
                .get()
                .await()
            !snapshot1.isEmpty || !snapshot2.isEmpty
        } catch (e: Exception) {
            false
        }
    }
}






