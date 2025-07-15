package com.example.tiktok.Model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import com.google.firebase.Timestamp
import com.google.firebase.firestore.PropertyName
import com.google.firebase.firestore.Exclude

@Parcelize
data class MarketModel(
    val imageUrl: String = "",
    @get:Exclude val localImageResId: Int? = null, //忽略 Firestore
    val title: String = "",
    val caption: String = "",
    val userId: String = "",
    val postId: String = "",
    val timestamp: Timestamp? = null,
    @PropertyName("product")
    val isProduct: Boolean = false,
    val price: Double? = null
) : Parcelable