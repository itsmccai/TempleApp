package com.example.tiktok.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.firebase.Timestamp

@Parcelize
data class PostModel(
    val imageUrl: String = "",
    val title: String = "",
    val caption: String = "",
    val userId: String = "",
    val postId: String = "",   // 可选：用于存评论
    val timestamp:  Timestamp ?= null  //创建时间戳
): Parcelable