package com.example.tiktok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tiktok.Adapter.ChatPreviewAdapter
import com.example.tiktok.Model.ChatModel
import com.example.tiktok.databinding.FragmentChatListBinding

class ChatListFragment : Fragment() {
    private lateinit var binding: FragmentChatListBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatListBinding.inflate(inflater, container, false)

        val ChatList = listOf(
            ChatModel("1", "Momo", R.drawable.default_avatar, "Hey, what's up?", 162515),
            ChatModel("2", "Yuki", R.drawable.default_avatar, "Are you coming?", 162523)
        )

        binding.chatListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.chatListRecyclerView.adapter = ChatPreviewAdapter(ChatList)

        return binding.root
    }
}
