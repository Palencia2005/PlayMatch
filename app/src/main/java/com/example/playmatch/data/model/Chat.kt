package com.example.playmatch.data.model

data class Chat(
    val id: String = "",
    val matchId: String = "",
    val userId1: String = "",
    val userId2: String = "",
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis(),
    val unreadCount1: Int = 0,
    val unreadCount2: Int = 0
)






