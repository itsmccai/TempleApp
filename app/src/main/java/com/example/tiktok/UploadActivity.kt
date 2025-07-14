package com.example.tiktok

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.tiktok.Model.PostModel
import com.example.tiktok.Model.MarketModel
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
    private var isUploading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val imageUriStr = intent.getStringExtra("imageUri")
        val imageUri = Uri.parse(imageUriStr)
        binding.imagePreview.setImageURI(imageUri)

        // 显示/隐藏价格输入框
        binding.checkboxIsProduct.setOnCheckedChangeListener { _, isChecked ->
            binding.editPrice.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        binding.postButton.setOnClickListener {
            if (isUploading) return@setOnClickListener

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

    private fun uploadToFirebase(uri: Uri, title: String, caption: String) {
        val filename = "posts/${System.currentTimeMillis()}.jpg"
        val fileRef = storageRef.child(filename)

        fileRef.putFile(uri)
            .continueWithTask { task ->
                if (!task.isSuccessful) throw task.exception!!
                fileRef.downloadUrl
            }
            .addOnSuccessListener { downloadUrl ->
                val isProduct = binding.checkboxIsProduct.isChecked
                val price = if (isProduct) binding.editPrice.text.toString().toDoubleOrNull() else null

                val userId = currentUser?.uid ?: "anonymous"
                val timestamp = Timestamp.now()

                val collection = if (isProduct) "products" else "posts"

                if (isProduct) {
                    // 保存为 MarketModel
                    val marketPost = MarketModel(
                        imageUrl = downloadUrl.toString(),
                        title = title,
                        caption = caption,
                        userId = userId,
                        timestamp = timestamp,
                        isProduct = true,
                        price = price
                    )
                    db.collection("products")
                        .add(marketPost)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Product posted!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            restoreUI("Upload failed: ${it.message}")
                        }
                } else {
                    // 保存为 PostModel
                    val normalPost = PostModel(
                        imageUrl = downloadUrl.toString(),
                        title = title,
                        caption = caption,
                        userId = userId,
                        timestamp = timestamp,
                        isProduct = false,
                        price = null
                    )
                    db.collection("posts")
                        .add(normalPost)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Post published!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        .addOnFailureListener {
                            restoreUI("Upload failed: ${it.message}")
                        }
                }
            }
            .addOnFailureListener {
                restoreUI("Upload failed: ${it.message}")
            }
    }

    private fun restoreUI(errorMsg: String) {
        isUploading = false
        binding.postButton.isEnabled = true
        binding.postButton.text = "发布"
        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show()
    }
}
