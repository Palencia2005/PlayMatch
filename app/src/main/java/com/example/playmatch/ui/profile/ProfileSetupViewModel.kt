package com.example.playmatch.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playmatch.data.model.UserProfile
import com.example.playmatch.data.repository.ProfileRepository
import kotlinx.coroutines.launch

class ProfileSetupViewModel : ViewModel() {
    private val profileRepository = ProfileRepository()
    
    private val _profileSaved = MutableLiveData<Boolean>()
    val profileSaved: LiveData<Boolean> = _profileSaved
    
    private var age = 0
    private var gender = ""
    private var height = ""
    private var musicTaste = listOf<String>()
    private var lookingFor = ""
    private var preferredGender = ""
    
    fun setAge(age: Int) {
        this.age = age
    }
    
    fun setGender(gender: String) {
        this.gender = gender
    }
    
    fun setHeight(height: String) {
        this.height = height
    }
    
    fun setMusicTaste(musicTaste: List<String>) {
        this.musicTaste = musicTaste
    }
    
    fun setLookingFor(lookingFor: String) {
        this.lookingFor = lookingFor
    }
    
    fun setPreferredGender(preferredGender: String) {
        this.preferredGender = preferredGender
    }
    
    fun saveProfile(userId: String) {
        viewModelScope.launch {
            val profile = UserProfile(
                userId = userId,
                age = age,
                gender = gender,
                height = height,
                musicTaste = musicTaste,
                lookingFor = lookingFor,
                preferredGender = preferredGender
            )
            
            val result = profileRepository.saveProfile(profile)
            _profileSaved.value = result.isSuccess
        }
    }
}



