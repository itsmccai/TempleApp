package com.example.tiktok.Model

import com.google.firebase.Timestamp

data class PostModel(
    val imageUrl: String = "",
    val title: String = "",
    val caption: String = "",
    val userId: String = "",
    val timestamp:  Timestamp ?= null  //创建时间戳

    //like button and count

)