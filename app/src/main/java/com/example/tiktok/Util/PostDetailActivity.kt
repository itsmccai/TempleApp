package com.example.tiktok.Util

import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.tiktok.Model.MarketModel
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

        // 获取 PostModel 或 MarketModel
        val post: Parcelable? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("post", PostModel::class.java)
                ?: intent.getParcelableExtra("post", MarketModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("post") as? PostModel
                ?: intent.getParcelableExtra("post") as? MarketModel
        }

        post?.let {
            // 使用反射统一访问字段
            val title = it.javaClass.getMethod("getTitle").invoke(it) as? String ?: ""
            val caption = it.javaClass.getMethod("getCaption").invoke(it) as? String ?: ""
            val imageUrl = it.javaClass.getMethod("getImageUrl").invoke(it) as? String ?: ""
            val userId = it.javaClass.getMethod("getUserId").invoke(it) as? String ?: ""

            binding.postTitle.text = title
            binding.postCaption.text = caption
            Glide.with(this).load(imageUrl).into(binding.postImage)

            // 可选：显示价格（仅商品）
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

            // 加载用户信息
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { doc ->
                    val username = doc.getString("username") ?: "Unknown"
                    val avatarUrl = doc.getString("avatarUrl")
                    binding.userName.text = username

                    if (!avatarUrl.isNullOrEmpty()) {
                        Glide.with(this).load(avatarUrl).into(binding.userAvatar)
                    } else {
                        binding.userAvatar.setImageResource(R.drawable.default_avatar)
                    }
                }

            // 评论按钮点击事件（可拓展）
            binding.sendButton.setOnClickListener {
                // TODO: 添加评论上传逻辑
            }
        }
    }
}
