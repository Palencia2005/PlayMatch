package com.example.playmatch.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playmatch.data.model.Chat
import com.example.playmatch.data.repository.ChatRepository
import kotlinx.coroutines.launch

class ChatsViewModel : ViewModel() {
    private val chatRepository = ChatRepository()
    
    private val _chats = MutableLiveData<List<Chat>>()
    val chats: LiveData<List<Chat>> = _chats
    
    fun loadChats(userId: String) {
        viewModelScope.launch {
            val userChats = chatRepository.getUserChats(userId)
            _chats.value = userChats
        }
    }
}






