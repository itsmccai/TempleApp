package com.example.tiktok.Model

import com.google.firebase.Timestamp

data class PostModel(
    val imageResId: Int,
    val description: String,
    val createdTime:  Timestamp = Timestamp.now()  //创建时间戳
)