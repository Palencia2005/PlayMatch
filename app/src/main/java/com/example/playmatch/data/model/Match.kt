package com.example.playmatch.data.model

data class Match(
    val id: String = "",
    val userId1: String = "",
    val userId2: String = "",
    val matchedAt: Long = System.currentTimeMillis(),
    val user1Name: String = "",
    val user2Name: String = "",
    val user1Photo: String = "",
    val user2Photo: String = ""
)






