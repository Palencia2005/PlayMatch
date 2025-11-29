package com.example.playmatch.ui.discovery

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playmatch.data.model.Match
import com.example.playmatch.data.model.UserProfile
import com.example.playmatch.data.repository.MatchRepository
import com.example.playmatch.data.repository.ProfileRepository
import kotlinx.coroutines.launch
import java.util.UUID

class DiscoveryViewModel : ViewModel() {
    private val profileRepository = ProfileRepository()
    private val matchRepository = MatchRepository()
    
    private val _profiles = MutableLiveData<List<UserProfile>>()
    val profiles: LiveData<List<UserProfile>> = _profiles
    
    private val _matchCreated = MutableLiveData<String>()
    val matchCreated: LiveData<String> = _matchCreated
    
    fun loadProfiles(currentUserId: String) {
        viewModelScope.launch {
            val allProfiles = profileRepository.getAllProfiles(currentUserId)
            _profiles.value = allProfiles
        }
    }
    
    fun likeProfile(profile: UserProfile) {
        viewModelScope.launch {
            // Get current user ID from AuthRepository
            val authRepository = com.example.playmatch.data.repository.AuthRepository()
            val currentUserId = authRepository.getCurrentUserId() ?: return@launch
            
            // Check if match already exists
            val matchExists = matchRepository.checkMatchExists(currentUserId, profile.userId)
            
            if (!matchExists) {
                // Create a match (in a real app, you'd check if both users liked each other)
                val match = Match(
                    id = UUID.randomUUID().toString(),
                    userId1 = currentUserId,
                    userId2 = profile.userId,
                    user1Name = "",
                    user2Name = ""
                )
                
                val result = matchRepository.createMatch(match)
                if (result.isSuccess) {
                    _matchCreated.value = match.id
                }
            }
        }
    }
}

