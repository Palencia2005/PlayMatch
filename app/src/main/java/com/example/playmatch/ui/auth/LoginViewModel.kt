package com.example.playmatch.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playmatch.data.repository.AuthRepository
import com.example.playmatch.data.repository.ProfileRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val authRepository: AuthRepository by lazy {
        try {
            AuthRepository()
        } catch (e: Exception) {
            android.util.Log.e("LoginViewModel", "Error creando AuthRepository", e)
            AuthRepository() // Intentar de nuevo
        }
    }
    
    private val profileRepository = ProfileRepository()
    
    private val _loginResult = MutableLiveData<Result<String>>()
    val loginResult: LiveData<Result<String>> = _loginResult
    
    private val _hasProfile = MutableLiveData<Boolean>()
    val hasProfile: LiveData<Boolean> = _hasProfile
    
    private val _passwordResetResult = MutableLiveData<Result<Unit>>()
    val passwordResetResult: LiveData<Result<Unit>> = _passwordResetResult
    
    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val result = authRepository.signInWithEmail(email, password)
                if (result.isSuccess) {
                    val userId = result.getOrNull() ?: ""
                    // Verificar si tiene perfil
                    val profile = profileRepository.getProfile(userId)
                    _hasProfile.value = profile != null && profile.age > 0
                }
                _loginResult.value = result
            } catch (e: Exception) {
                android.util.Log.e("LoginViewModel", "Error en login", e)
                _loginResult.value = Result.failure(e)
            }
        }
    }
    
    fun sendPasswordResetEmail(email: String) {
        viewModelScope.launch {
            try {
                val result = authRepository.sendPasswordResetEmail(email)
                _passwordResetResult.value = result
            } catch (e: Exception) {
                android.util.Log.e("LoginViewModel", "Error enviando email de recuperación", e)
                _passwordResetResult.value = Result.failure(e)
            }
        }
    }
}



