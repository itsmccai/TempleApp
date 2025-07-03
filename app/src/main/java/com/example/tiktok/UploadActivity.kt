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
    lateinit var binding:ActivityUploadBinding
    private val storageRef = FirebaseStorage.getInstance().reference
    private val db = FirebaseFirestore.getInstance()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        val imageUriStr = intent.getStringExtra("imageUri")
        val imageUri = Uri.parse(imageUriStr)

        binding.imagePreview.setImageURI(imageUri)

        binding.postButton.setOnClickListener {
            val postTitle = binding.editTitle.text.toString().trim()
            val postCaption = binding.editCaption.text.toString().trim()

            if (postTitle.isNotEmpty() && postCaption.isNotEmpty())
            {
                uploadToFirebase(imageUri, postTitle, postCaption)
            }
            else
            {
                Toast.makeText(this, "请输入标题和说明", Toast.LENGTH_SHORT).show()
            }
        }
    }

    //functions:
    fun uploadToFirebase(uri:Uri, title:String, caption:String)
    {
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
                        Toast.makeText(this, "发布成功", Toast.LENGTH_SHORT).show()
                        finish()  // 返回主页
                    }
            }
            .addOnFailureListener {
                Toast.makeText(this, "上传失败: ${it.message}", Toast.LENGTH_SHORT).show()
            }

    }
}