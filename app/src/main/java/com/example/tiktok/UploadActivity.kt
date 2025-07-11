package com.example.tiktok

import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tiktok.Model.PostModel
import com.example.tiktok.databinding.ActivityUploadBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class UploadActivity : AppCompatActivity() {
    lateinit var binding: ActivityUploadBinding
    private val storageRef = FirebaseStorage.getInstance().reference
    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private var isUploading = false  // 防止重复点击

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val imageUriStr = intent.getStringExtra("imageUri")
        val imageUri = Uri.parse(imageUriStr)

        binding.imagePreview.setImageURI(imageUri)

        binding.postButton.setOnClickListener {
            if (isUploading) return@setOnClickListener // 如果正在上传，阻止再次触发

            val postTitle = binding.editTitle.text.toString().trim()
            val postCaption = binding.editCaption.text.toString().trim()

            if (postTitle.isNotEmpty() && postCaption.isNotEmpty()) {
                isUploading = true
                binding.postButton.isEnabled = false
                binding.postButton.text = "posting..."
                uploadToFirebase(imageUri, postTitle, postCaption)
            } else {
                Toast.makeText(this, "plz fill out the title and caption", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun uploadToFirebase(uri: Uri, title: String, caption: String) {
        val filename = "posts/${System.currentTimeMillis()}.jpg"
        val fileRef = storageRef.child(filename)

        fileRef.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception!!
                fileRef.downloadUrl
            }
            .addOnSuccessListener { downloadUrl ->
                val post = PostModel(
                    imageUrl = downloadUrl.toString(),
                    title = title,
                    caption = caption,
                    userId = currentUser?.uid ?: "anonymous",
                    timestamp = Timestamp.now()
                )

                db.collection("posts")
                    .add(post)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Post Successfully!", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    .addOnFailureListener {
                        restoreUI("上传图片成功但写入失败: ${it.message}")
                    }
            }
            .addOnFailureListener {
                restoreUI("上传失败: ${it.message}")
            }
    }

    private fun restoreUI(errorMsg: String) {
        isUploading = false
        binding.postButton.isEnabled = true
        binding.postButton.text = "发布"
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
    }
}
