package com.example.tiktok.Adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.tiktok.Model.ChatMessage
import com.example.tiktok.databinding.ItemChatMessageBinding

//used to show messages
class ChatMessageAdapter(private val userId: String, private val messages: List<ChatMessage>) :
    RecyclerView.Adapter<ChatMessageAdapter.ChatViewHolder>() {

    inner class ChatViewHolder(val binding: ItemChatMessageBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val binding = ItemChatMessageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val message = messages[position]
        with(holder.binding) {
            println("调试: senderId = ${message.senderId}, userId = $userId")

            if (message.senderId == userId) {
                senderText.text = message.message
                senderText.visibility = android.view.View.VISIBLE
                receiverText.visibility = android.view.View.GONE
            } else {
                receiverText.text = message.message
                receiverText.visibility = android.view.View.VISIBLE
                senderText.visibility = android.view.View.GONE
            }
        }
    }

    override fun getItemCount(): Int = messages.size
}
