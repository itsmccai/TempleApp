package com.example.tiktok.Model

//mutaablelist可变，list不可变 在kotlin里
data class UserModel(
    var id:String = "",
    var email:String = "",
    val avatarUrl: String = "",
    var username:String = "",
    var profilePic:String = "",
    var followerList:MutableList<String> = mutableListOf(),
    var followingList:MutableList<String> = mutableListOf()
    )
