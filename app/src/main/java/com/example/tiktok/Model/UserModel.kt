package com.example.tiktok.Model

import com.example.tiktok.R

//mutaablelist可变，list不可变 在kotlin里
data class UserModel(
    var id:String = "",
    var email:String = "",
    var username:String = "",
    var avatarResId: Int = R.drawable.default_avatar,
    var followerList:MutableList<String> = mutableListOf(),
    var followingList:MutableList<String> = mutableListOf()
    )
