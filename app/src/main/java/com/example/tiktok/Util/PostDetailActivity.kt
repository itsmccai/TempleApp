package com.example.tiktok.Util

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.tiktok.Model.PostModel
import com.example.tiktok.databinding.ActivityPostDetailBinding
import com.google.firebase.firestore.FirebaseFirestore
//TODO: 默认头像 ➕ 退回主页功能

class PostDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostDetailBinding
    private val db = FirebaseFirestore.getInstance() // Firebase Firestore 实例，用于获取user信息

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ 使用兼容 API 33+ 的方式安全获取 Parcelable 对象
        val post = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("post", PostModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("post") as? PostModel
        }

        // post不为空的时候 获取adapter传递过来的post信息
        post?.let {
            binding.postTitle.text = it.title
            binding.postCaption.text = it.caption
            Glide.with(this).load(it.imageUrl).into(binding.postImage)

            // 获取用户信息
            db.collection("users").document(it.userId)
                .get().addOnSuccessListener { doc ->
                    val username = doc.getString("username")
                    val avatarUrl = doc.getString("avatarUrl")
                    binding.userName.text = username ?: "Unknown"
                    Glide.with(this).load(avatarUrl).into(binding.userAvatar)
                }

            // 评论发送按钮点击事件
            binding.sendButton.setOnClickListener {
                // TODO: 你可以在这里添加评论上传到 Firestore 的逻辑
            }
        }
    }
}
