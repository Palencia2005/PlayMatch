package com.example.playmatch.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val profileCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)






