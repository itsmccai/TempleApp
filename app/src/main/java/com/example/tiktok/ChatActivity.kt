package com.example.tiktok

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.tiktok.Adapter.ChatMessageAdapter
import com.example.tiktok.Model.ChatMessage
import com.example.tiktok.databinding.ActivityChatBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var dbRef: DatabaseReference
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val messageList = mutableListOf<ChatMessage>()
    private lateinit var chatmessageAdapter: ChatMessageAdapter

    private lateinit var chatUserId: String
    private lateinit var chatUserName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 获取传递过来的聊天对象信息
        chatUserId = intent.getStringExtra("chatUserId") ?: ""
        chatUserName = intent.getStringExtra("chatUserName") ?: "Unknown"
        binding.usernameText.text = chatUserName

        setupRecyclerView()
        listenToMessages()

        binding.btnSend.setOnClickListener {
            val messageText = binding.messageInput.text.toString().trim()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
            } else {
                Toast.makeText(this, "请输入内容", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        val currentUserId = currentUser?.uid ?: "anonymous"
        chatmessageAdapter = ChatMessageAdapter(currentUserId, messageList)
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = chatmessageAdapter
    }

    private fun listenToMessages() {
        val currentUserId = currentUser?.uid ?: "anonymous"
        val chatRoomId = getChatRoomId(currentUserId, chatUserId)
        dbRef = FirebaseDatabase.getInstance().getReference("messages").child(chatRoomId)

        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                messageList.clear()
                for (msgSnap in snapshot.children) {
                    msgSnap.getValue(ChatMessage::class.java)?.let {
                        messageList.add(it)
                    }
                }
                chatmessageAdapter.notifyDataSetChanged()
                binding.chatRecyclerView.scrollToPosition(messageList.size - 1)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatActivity, "加载消息失败", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun sendMessage(content: String) {
        val senderId = currentUser?.uid ?: "anonymous"
        val receiverId = chatUserId

        val message = ChatMessage(
            senderId = senderId,
            receiverId = receiverId,
            message = content
        )

        val chatRoomId = getChatRoomId(senderId, receiverId)
        dbRef = FirebaseDatabase.getInstance().getReference("messages").child(chatRoomId)

        dbRef.push().setValue(message)
            .addOnSuccessListener {
                binding.messageInput.setText("")

                // 🔁 更新 Firestore 的 chatList（当前用户和接收者都可见）
                val timestamp = com.google.firebase.Timestamp.now()
                val senderName = FirebaseAuth.getInstance().currentUser?.email?.substringBefore("@") ?: "Me"

                val chatForSender = hashMapOf(
                    "userId" to receiverId,
                    "userName" to chatUserName,
                    "fromId" to senderId,
                    "toId" to receiverId,
                    "lastMessage" to content,
                    "timestamp" to timestamp
                )

                val chatForReceiver = hashMapOf(
                    "userId" to senderId,
                    "userName" to senderName,
                    "fromId" to receiverId,
                    "toId" to senderId,
                    "lastMessage" to content,
                    "timestamp" to timestamp
                )

                val db = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                db.collection("chatList")
                    .document(senderId + "_" + receiverId)
                    .set(chatForSender)

                db.collection("chatList")
                    .document(receiverId + "_" + senderId)
                    .set(chatForReceiver)
            }
            .addOnFailureListener {
                Toast.makeText(this, "发送失败: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun getChatRoomId(id1: String, id2: String): String {
        return if (id1 < id2) "$id1-$id2" else "$id2-$id1"
    }
}
