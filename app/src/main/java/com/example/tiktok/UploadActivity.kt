package com.example.tiktok

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tiktok.databinding.ActivityUploadBinding

class UploadActivity : AppCompatActivity() {
    lateinit var binding:ActivityUploadBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        // 获取 MainActivity 传来的 imageUri 字符串
        val imageUriStr = intent.getStringExtra("imageUri")
        if (!imageUriStr.isNullOrEmpty()) {
            val imageUri = android.net.Uri.parse(imageUriStr)
            binding.imagePreview.setImageURI(imageUri) // ✅ 显示图片到预览区域
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}