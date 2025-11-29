package com.example.playmatch.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playmatch.data.model.User
import com.example.playmatch.data.model.UserProfile
import com.example.playmatch.data.repository.AuthRepository
import com.example.playmatch.data.repository.ProfileRepository
import kotlinx.coroutines.launch

data class ProfileUiState(
    val user: User? = null,
    val profile: UserProfile? = null
)

class ProfileViewModel : ViewModel() {

    private val profileRepository = ProfileRepository()
    private val authRepository = AuthRepository()

    private val _profileState = MutableLiveData(ProfileUiState())
    val profileState: LiveData<ProfileUiState> = _profileState

    fun loadProfile(userId: String) {
        viewModelScope.launch {
            val profile = profileRepository.getProfile(userId)
            val user = authRepository.getUserById(userId)
            _profileState.value = ProfileUiState(user = user, profile = profile)
        }
    }
}




