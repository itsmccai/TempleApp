package com.example.tiktok.Util

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.tiktok.Model.PostModel
import com.example.tiktok.R
import com.example.tiktok.databinding.ActivityPostDetailBinding
import com.google.firebase.firestore.FirebaseFirestore

class PostDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostDetailBinding
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 获取 Post 对象
        val post = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("post", PostModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("post") as? PostModel
        }

        post?.let {
            binding.postTitle.text = it.title
            binding.postCaption.text = it.caption
            Glide.with(this).load(it.imageUrl).into(binding.postImage)

            // 加载用户信息
            db.collection("users").document(it.userId)
                .get()
                .addOnSuccessListener { doc ->
                    val username = doc.getString("username") ?: "Unknown"
                    val avatarUrl = doc.getString("avatarUrl") // 可能为 null
                    binding.userName.text = username

                    if (!avatarUrl.isNullOrEmpty()) {
                        // 有网络头像链接就加载网络图片
                        Glide.with(this).load(avatarUrl).into(binding.userAvatar)
                    } else {
                        // 没有头像链接就加载本地默认头像
                        binding.userAvatar.setImageResource(R.drawable.default_avatar)
                    }
                }

            // 评论按钮点击事件
            binding.sendButton.setOnClickListener {
                // TODO: 添加评论上传逻辑
            }
        }
    }
}
