package com.example.playmatch.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playmatch.data.model.User
import com.example.playmatch.data.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterViewModel : ViewModel() {
    private val authRepository = AuthRepository()
    private val auth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            android.util.Log.e("RegisterViewModel", "Error inicializando Firebase Auth", e)
            null
        }
    }
    
    private val _registrationResult = MutableLiveData<Result<String>>()
    val registrationResult: LiveData<Result<String>> = _registrationResult
    
    private val _emailExists = MutableLiveData<Boolean>()
    val emailExists: LiveData<Boolean> = _emailExists
    
    private val _phoneExists = MutableLiveData<Boolean>()
    val phoneExists: LiveData<Boolean> = _phoneExists
    
    fun checkEmailAndPhone(email: String, phoneNumber: String, callback: (Boolean, Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                android.util.Log.d("RegisterViewModel", "Verificando email y teléfono: email=$email, phone=$phoneNumber")
                
                val emailExists = if (email.isNotEmpty()) {
                    authRepository.checkEmailExists(email)
                } else {
                    false
                }
                
                val phoneExists = if (phoneNumber.isNotEmpty()) {
                    authRepository.checkPhoneExists(phoneNumber)
                } else {
                    false
                }
                
                android.util.Log.d("RegisterViewModel", "Resultado verificación: emailExists=$emailExists, phoneExists=$phoneExists")
                
                _emailExists.value = emailExists
                _phoneExists.value = phoneExists
                
                callback(emailExists, phoneExists)
            } catch (e: Exception) {
                android.util.Log.e("RegisterViewModel", "Error verificando email/teléfono", e)
                // Si hay error, asumir que no existen para permitir continuar
                callback(false, false)
            }
        }
    }
    
    fun registerUser(name: String, email: String, password: String, phoneNumber: String) {
        viewModelScope.launch {
            try {
                android.util.Log.d("RegisterViewModel", "Registrando usuario: name=$name, email=$email, phone=$phoneNumber")
                
                // Primero crear el usuario en Firebase Auth con email y contraseña
                val authResult = authRepository.createUserWithEmailAndPassword(email, password)
                
                val userId = authResult.getOrNull() ?: run {
                    val error = authResult.exceptionOrNull() ?: Exception("Error desconocido")
                    android.util.Log.e("RegisterViewModel", "Error creando usuario en Auth", error)
                    _registrationResult.value = Result.failure(error)
                    return@launch
                }
                
                android.util.Log.d("RegisterViewModel", "Usuario creado en Auth con ID: $userId")
                
                // Luego crear el documento del usuario en Firestore
                val user = User(
                    id = userId,
                    name = name,
                    email = email,
                    phoneNumber = phoneNumber
                )
                
                android.util.Log.d("RegisterViewModel", "Guardando datos del usuario en Firestore")
                val result = authRepository.createUser(user)
                
                result.getOrElse { error ->
                    android.util.Log.e("RegisterViewModel", "Error guardando datos del usuario", error)
                }
                
                result.getOrNull()?.let {
                    android.util.Log.d("RegisterViewModel", "Usuario creado exitosamente con ID: $it")
                }
                
                _registrationResult.value = result
            } catch (e: Exception) {
                android.util.Log.e("RegisterViewModel", "Excepción al registrar usuario", e)
                _registrationResult.value = Result.failure(e)
            }
        }
    }
}

