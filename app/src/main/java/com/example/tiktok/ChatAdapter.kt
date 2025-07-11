package com.example.tiktok.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tiktok.ChatActivity
import com.example.tiktok.Model.ChatModel
import com.example.tiktok.databinding.ItemChatBinding

class ChatPreviewAdapter(private val chatList: List<ChatModel>) :
    RecyclerView.Adapter<ChatPreviewAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(val binding: ItemChatBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = chatList[position]
        with(holder.binding) {
            username.text = chat.userName
            lastMessage.text = chat.lastMessage
            Glide.with(avatar.context).load(chat.avatarUrl).into(avatar)

            root.setOnClickListener {
                val context = it.context
                val intent = Intent(context, ChatActivity::class.java)
                intent.putExtra("chatUserId", chat.userId)
                intent.putExtra("chatUserName", chat.userName)
                intent.putExtra("chatUserAvatar", chat.avatarUrl)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int = chatList.size
}
