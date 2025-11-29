package com.example.playmatch.data.repository

import com.example.playmatch.data.model.Chat
import com.example.playmatch.data.model.Message
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ChatRepository {
    private val db = FirebaseFirestore.getInstance()
    
    suspend fun createChat(chat: Chat): Result<String> {
        return try {
            db.collection("chats").document(chat.id).set(chat).await()
            Result.success(chat.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserChats(userId: String): List<Chat> {
        return try {
            val snapshot1 = db.collection("chats")
                .whereEqualTo("userId1", userId)
                .get()
                .await()
            val snapshot2 = db.collection("chats")
                .whereEqualTo("userId2", userId)
                .get()
                .await()
            
            val chats1 = snapshot1.documents.mapNotNull { it.toObject(Chat::class.java) }
            val chats2 = snapshot2.documents.mapNotNull { it.toObject(Chat::class.java) }
            (chats1 + chats2).sortedByDescending { it.lastMessageTime }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getChatById(chatId: String): Chat? {
        return try {
            val document = db.collection("chats").document(chatId).get().await()
            document.toObject(Chat::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    suspend fun sendMessage(message: Message): Result<String> {
        return try {
            db.collection("chats").document(message.chatId)
                .collection("messages")
                .document(message.id)
                .set(message)
                .await()
            
            // Update chat last message
            db.collection("chats").document(message.chatId)
                .update(
                    mapOf(
                        "lastMessage" to message.text,
                        "lastMessageTime" to message.timestamp
                    )
                )
                .await()
            
            Result.success(message.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getChatMessages(chatId: String): List<Message> {
        return try {
            val snapshot = db.collection("chats").document(chatId)
                .collection("messages")
                .orderBy("timestamp")
                .get()
                .await()
            snapshot.documents.mapNotNull { it.toObject(Message::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}






