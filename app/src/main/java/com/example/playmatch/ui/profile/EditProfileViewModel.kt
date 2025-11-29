package com.example.playmatch.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playmatch.data.model.UserProfile
import com.example.playmatch.data.repository.ProfileRepository
import kotlinx.coroutines.launch

class EditProfileViewModel : ViewModel() {
    private val profileRepository = ProfileRepository()
    
    private val _profile = MutableLiveData<UserProfile?>()
    val profile: LiveData<UserProfile?> = _profile
    
    private val _profileSaved = MutableLiveData<Boolean>()
    val profileSaved: LiveData<Boolean> = _profileSaved
    
    fun loadProfile(userId: String) {
        viewModelScope.launch {
            val profile = profileRepository.getProfile(userId)
            _profile.value = profile
        }
    }
    
    fun updateProfile(
        userId: String,
        age: Int,
        gender: String,
        height: String,
        musicTaste: List<String>,
        lookingFor: String,
        preferredGender: String
    ) {
        viewModelScope.launch {
            val currentProfile = _profile.value
            val updatedProfile = currentProfile?.copy(
                age = age,
                gender = gender,
                height = height,
                musicTaste = musicTaste,
                lookingFor = lookingFor,
                preferredGender = preferredGender
            ) ?: UserProfile(
                userId = userId,
                age = age,
                gender = gender,
                height = height,
                musicTaste = musicTaste,
                lookingFor = lookingFor,
                preferredGender = preferredGender
            )
            
            val result = profileRepository.saveProfile(updatedProfile)
            _profileSaved.value = result.isSuccess
            if (result.isSuccess) {
                _profile.value = updatedProfile
            }
        }
    }
}

