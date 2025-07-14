package com.example.tiktok.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName

@Parcelize
data class MarketModel(
    val imageUrl: String = "",
    val title: String = "",
    val caption: String = "",
    val userId: String = "",
    val postId: String = "",   // 可选：用于存评论
    val timestamp:  Timestamp ?= null,
    @PropertyName("product")
    val isProduct: Boolean = false,
    val price: Double? = null // price
): Parcelable