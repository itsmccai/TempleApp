package com.example.tiktok

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tiktok.Adapter.ChatAdapter
import com.example.tiktok.Model.ChatModel
import com.example.tiktok.databinding.FragmentChatListBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class ChatListFragment : Fragment() {
    private lateinit var binding: FragmentChatListBinding
    private lateinit var chatAdapter: ChatAdapter
    private val chatList = mutableListOf<ChatModel>()
    private val db = FirebaseFirestore.getInstance()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChatListBinding.inflate(inflater, container, false)

        binding.chatListRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        chatAdapter = ChatAdapter(chatList)
        binding.chatListRecyclerView.adapter = chatAdapter

        loadChatList()

        return binding.root
    }

    private fun loadChatList() {
        if (currentUserId == null) {
            Toast.makeText(requireContext(), "用户未登录", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("chatList")
            .whereEqualTo("fromId", currentUserId)
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) return@addSnapshotListener

                chatList.clear()
                for (doc in snapshot.documents) {
                    val chat = doc.toObject(ChatModel::class.java)
                    chat?.let { chatList.add(it) }
                }
                chatAdapter.notifyDataSetChanged()
            }
    }

}
