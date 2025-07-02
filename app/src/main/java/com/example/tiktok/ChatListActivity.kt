package com.example.tiktok

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tiktok.databinding.ActivityChatListBinding

class ChatListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatListBinding
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityChatListBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        //TODO: load chats by using recyclerview
    }
}