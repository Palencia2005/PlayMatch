package com.example.playmatch.ui.auth

import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Random
import java.util.concurrent.TimeUnit

class VerificationViewModel : ViewModel() {
    private val auth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            android.util.Log.e("VerificationViewModel", "Error inicializando Firebase Auth", e)
            null
        }
    }
    private var storedVerificationId: String? = null
    private var resendToken: PhoneAuthProvider.ForceResendingToken? = null
    
    private val _verificationResult = MutableLiveData<Result<Unit>>()
    val verificationResult: LiveData<Result<Unit>> = _verificationResult
    
    // For email verification, we'll use a simple code system
    private var emailVerificationCode: String? = null
    
    fun sendEmailVerificationCode(email: String) {
        viewModelScope.launch {
            try {
                // Generate a 6-digit code
                val code = String.format("%06d", Random().nextInt(1000000))
                emailVerificationCode = code
                
                // In a real app, you would send this via email service
                // For now, we'll just store it
                // TODO: Integrate with email service (Firebase Functions, SendGrid, etc.)
                
                _verificationResult.value = Result.success(Unit)
            } catch (e: Exception) {
                _verificationResult.value = Result.failure(e)
            }
        }
    }
    
    fun sendPhoneVerificationCode(phoneNumber: String, activity: Activity) {
        val firebaseAuth = auth ?: run {
            _verificationResult.value = Result.failure(Exception("Firebase Auth no está disponible. Configura Firebase correctamente."))
            return
        }
        
        val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                verifyPhoneCode(credential.smsCode ?: "")
            }
            
            override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                _verificationResult.value = Result.failure(e)
            }
            
            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                storedVerificationId = verificationId
                resendToken = token
            }
        }
        
        val options = PhoneAuthOptions.newBuilder(firebaseAuth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(callbacks)
            .build()
        
        PhoneAuthProvider.verifyPhoneNumber(options)
    }
    
    fun verifyEmailCode(code: String) {
        viewModelScope.launch {
            try {
                if (code == emailVerificationCode) {
                    _verificationResult.value = Result.success(Unit)
                } else {
                    _verificationResult.value = Result.failure(Exception("Código inválido"))
                }
            } catch (e: Exception) {
                _verificationResult.value = Result.failure(e)
            }
        }
    }
    
    fun verifyPhoneCode(code: String) {
        val firebaseAuth = auth ?: run {
            _verificationResult.value = Result.failure(Exception("Firebase Auth no está disponible. Configura Firebase correctamente."))
            return
        }
        
        viewModelScope.launch {
            try {
                val verificationId = storedVerificationId
                if (verificationId != null && code.isNotEmpty()) {
                    val credential = PhoneAuthProvider.getCredential(verificationId, code)
                    val result = firebaseAuth.signInWithCredential(credential).await()
                    _verificationResult.value = Result.success(Unit)
                } else {
                    _verificationResult.value = Result.failure(Exception("Código inválido"))
                }
            } catch (e: Exception) {
                _verificationResult.value = Result.failure(e)
            }
        }
    }
}

