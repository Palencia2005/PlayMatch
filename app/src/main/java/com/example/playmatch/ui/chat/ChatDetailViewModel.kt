package com.example.playmatch.ui.chat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playmatch.data.model.Message
import com.example.playmatch.data.repository.ChatRepository
import kotlinx.coroutines.launch
import java.util.UUID

class ChatDetailViewModel : ViewModel() {
    private val chatRepository = ChatRepository()
    
    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages
    
    fun loadMessages(chatId: String) {
        viewModelScope.launch {
            val chatMessages = chatRepository.getChatMessages(chatId)
            _messages.value = chatMessages
        }
    }
    
    fun sendMessage(chatId: String, senderId: String, text: String) {
        viewModelScope.launch {
            // Get receiver ID from chat
            val chat = chatRepository.getChatById(chatId)
            val receiverId = if (chat?.userId1 == senderId) chat.userId2 else chat?.userId1 ?: ""
            
            val message = Message(
                id = UUID.randomUUID().toString(),
                chatId = chatId,
                senderId = senderId,
                receiverId = receiverId,
                text = text
            )
            
            chatRepository.sendMessage(message)
            loadMessages(chatId) // Reload messages
        }
    }
}






