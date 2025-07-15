package com.example.tiktok.Adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.tiktok.ChatActivity
import com.example.tiktok.Model.ChatModel
import com.example.tiktok.databinding.ItemChatBinding
import java.text.SimpleDateFormat
import java.util.*

class ChatAdapter(private val chatList: List<ChatModel>) :
    RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {

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
            timeText.text = formatTimestamp(chat.timestamp?.toDate()?.time ?: 0L)

            // 圆形头像加载（local resource or url）
            Glide.with(avatar.context)
                .load(chat.avatarUrl)
                .circleCrop()
                .into(avatar)

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

    private fun formatTimestamp(timestamp: Long): String {
        return try {
            val sdf = SimpleDateFormat("MM-dd", Locale.getDefault())
            sdf.format(Date(timestamp))
        } catch (e: Exception) {
            ""
        }
    }
}
