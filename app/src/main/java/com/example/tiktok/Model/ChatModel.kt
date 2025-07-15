package com.example.tiktok.Model

import com.example.tiktok.R
import com.google.firebase.Timestamp

data class ChatModel(
    val userId: String = "",              // 对方的 userId（用于跳转）
    val userName: String = "",
    val avatarUrl: Int = R.drawable.default_avatar,
    val lastMessage: String = "",
    val timestamp: Timestamp? = null,
    val fromId: String = "",              // 当前用户 ID（发起聊天人）
    val toId: String = ""                 // 对方用户 ID
)
