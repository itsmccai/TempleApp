package com.example.tiktok.Model

import com.example.tiktok.R

data class ChatModel(
    val userId: String = "",
    val userName: String = "",
    var avatarUrl: Int = R.drawable.default_avatar,
    val lastMessage: String = "",
    val timestamp: Long = 0L
)
