package com.example.tiktok.Util

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.tiktok.ChatActivity
import com.example.tiktok.Model.MarketModel
import com.example.tiktok.Model.PostModel
import com.example.tiktok.R
import com.example.tiktok.databinding.ActivityPostDetailBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class PostDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostDetailBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val post: Parcelable? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("post", PostModel::class.java)
                ?: intent.getParcelableExtra("post", MarketModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("post") as? PostModel
                ?: intent.getParcelableExtra("post") as? MarketModel
        }

        post?.let {
            val title = it.javaClass.getMethod("getTitle").invoke(it) as? String ?: ""
            val caption = it.javaClass.getMethod("getCaption").invoke(it) as? String ?: ""
            val imageUrl = it.javaClass.getMethod("getImageUrl").invoke(it) as? String ?: ""
            val userId = it.javaClass.getMethod("getUserId").invoke(it) as? String ?: ""

            binding.postTitle.text = title
            binding.postCaption.text = caption
            Glide.with(this).load(imageUrl).into(binding.postImage)

            val isProduct = try {
                it.javaClass.getMethod("isProduct").invoke(it) as? Boolean ?: false
            } catch (e: NoSuchMethodException) {
                false
            }

            val price = if (isProduct) {
                try {
                    it.javaClass.getMethod("getPrice").invoke(it) as? Double
                } catch (e: Exception) {
                    null
                }
            } else null

            if (isProduct && price != null) {
                binding.postCaption.text = "$caption\n\n价格：¥$price"
            }

            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

            // 加载用户信息
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { doc ->
                    val username = doc.getString("username") ?: "Unknown"
                    binding.userName.text = username

                    // 显示默认头像
                    binding.userAvatar.setImageResource(R.drawable.default_avatar)

                    // PostDetailActivity.kt - 修改 userAvatar 的点击事件
                    binding.userAvatar.setOnClickListener {
                        if (userId != currentUserId) {
                            val intent = Intent(this, ChatActivity::class.java).apply {
                                putExtra("chatUserId", userId)
                                putExtra("chatUserName", username)
                            }

                            val timestamp = com.google.firebase.Timestamp.now()

                            val currentUserName = FirebaseAuth.getInstance().currentUser?.email?.substringBefore("@") ?: "Me"

                            val chatForCurrent = hashMapOf(
                                "userId" to userId,
                                "userName" to username,
                                "fromId" to currentUserId,
                                "toId" to userId,
                                "lastMessage" to "",
                                "timestamp" to timestamp
                            )

                            val chatForTarget = hashMapOf(
                                "userId" to currentUserId,
                                "userName" to currentUserName,
                                "fromId" to userId,
                                "toId" to currentUserId,
                                "lastMessage" to "",
                                "timestamp" to timestamp
                            )

                            val db = FirebaseFirestore.getInstance()

                            // 当前用户的聊天列表
                            db.collection("chatList")
                                .document(currentUserId + "_" + userId)
                                .set(chatForCurrent)

                            // 对方的聊天列表（可选）
                            db.collection("chatList")
                                .document(userId + "_" + currentUserId)
                                .set(chatForTarget)

                            startActivity(intent)
                        }
                    }

                }

            // 评论按钮（可拓展）
            binding.sendButton.setOnClickListener {
                // TODO: 评论上传逻辑
            }
        }
    }
}
