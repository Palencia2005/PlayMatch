package com.example.playmatch.data.repository

import android.util.Log
import com.example.playmatch.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {
    private val auth: FirebaseAuth? by lazy { 
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error inicializando Firebase Auth", e)
            Log.e("AuthRepository", "Firebase no está configurado correctamente. Verifica google-services.json")
            null
        }
    }
    private val db: FirebaseFirestore? by lazy { 
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error inicializando Firestore", e)
            Log.e("AuthRepository", "Firebase no está configurado correctamente. Verifica google-services.json")
            null
        }
    }
    
    suspend fun checkEmailExists(email: String): Boolean {
        val firestore = db ?: run {
            Log.e("AuthRepository", "Firestore no está disponible")
            return false
        }
        return try {
            val snapshot = firestore.collection("users")
                .whereEqualTo("email", email)
                .limit(1)
                .get()
                .await()
            !snapshot.isEmpty
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error verificando email", e)
            false
        }
    }
    
    suspend fun checkPhoneExists(phoneNumber: String): Boolean {
        val firestore = db ?: run {
            Log.e("AuthRepository", "Firestore no está disponible")
            return false
        }
        return try {
            val snapshot = firestore.collection("users")
                .whereEqualTo("phoneNumber", phoneNumber)
                .limit(1)
                .get()
                .await()
            !snapshot.isEmpty
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error verificando teléfono", e)
            false
        }
    }
    
    suspend fun createUser(user: User): Result<String> {
        val firestore = db ?: return Result.failure(Exception("Firestore no está disponible. Configura Firebase correctamente."))
        return try {
            firestore.collection("users").document(user.id).set(user).await()
            Result.success(user.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signInWithEmail(email: String, password: String): Result<String> {
        val firebaseAuth = auth ?: return Result.failure(Exception("Firebase Auth no está disponible. Configura Firebase correctamente."))
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user?.uid ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun signInWithPhone(credential: PhoneAuthCredential): Result<String> {
        val firebaseAuth = auth ?: return Result.failure(Exception("Firebase Auth no está disponible. Configura Firebase correctamente."))
        return try {
            val result = firebaseAuth.signInWithCredential(credential).await()
            Result.success(result.user?.uid ?: "")
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserById(userId: String): User? {
        val firestore = db ?: run {
            Log.e("AuthRepository", "Firestore no está disponible")
            return null
        }
        return try {
            val document = firestore.collection("users").document(userId).get().await()
            document.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    fun getCurrentUserId(): String? {
        return auth?.currentUser?.uid
    }
    
    fun signOut() {
        auth?.signOut()
    }
    
    suspend fun createUserWithEmailAndPassword(email: String, password: String): Result<String> {
        val firebaseAuth = auth ?: return Result.failure(Exception("Firebase Auth no está disponible. Configura Firebase correctamente."))
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            Result.success(result.user?.uid ?: "")
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error creando usuario con email/password", e)
            Result.failure(e)
        }
    }
    
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        val firebaseAuth = auth ?: return Result.failure(Exception("Firebase Auth no está disponible. Configura Firebase correctamente."))
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Error enviando email de recuperación", e)
            Result.failure(e)
        }
    }
}

