package com.example.tiktok

import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tiktok.Model.UserModel
import com.example.tiktok.Util.UiUtil
import com.example.tiktok.databinding.ActivityLoginActicityBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern
import com.google.firebase.firestore.ktx.firestore
import android.content.Intent
import android.util.Log
import com.google.firebase.storage.FirebaseStorage


class LoginActicity : AppCompatActivity() {

    lateinit var binding:ActivityLoginActicityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginActicityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // 点击注册按钮
        binding.sumbitBtn.setOnClickListener{
            uploadDefaultAvatarToStorage()
            signup()
        }
        //点击下方文字跳转 登录页面
        //然后 finish（） 结束当前activity
        binding.goToLoginBtn.setOnClickListener{
            startActivity(Intent(this, LoginActivity_2::class.java))
            finish()
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    //上传头像：
    fun uploadDefaultAvatarToStorage() {
        val storageRef = FirebaseStorage.getInstance().reference
        val fileRef = storageRef.child("avatars/default_avatar.png")
        val inputStream = resources.openRawResource(R.drawable.default_avater)

        fileRef.putStream(inputStream)
            .continueWithTask { fileRef.downloadUrl }
            .addOnSuccessListener { uri ->
                Log.d("UPLOAD", "上传成功: $uri")
                Toast.makeText(this, "链接: $uri", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Log.e("UPLOAD", "上传失败: ${it.localizedMessage}")
            }
    }


    //progress bar 加载
    fun setInProgress(inProgress:Boolean){
        if(inProgress){
            binding.progressBar.visibility = View.VISIBLE
            binding.sumbitBtn.visibility = View.GONE
        }else{
            binding.progressBar.visibility = View.GONE
            binding.sumbitBtn.visibility = View.VISIBLE

        }


    }
    //登录逻辑
    fun signup(){
        val email = binding.emailInput.text.toString()
        val password = binding.passwordInput.text.toString()
        val confirmpassword = binding.confirmPasswordInput.text.toString()

        //Prerequisites
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches() || !email.endsWith("@temple.edu")){
            binding.emailInput.setError("Please use vaild temple account!")
            return;
        }
        if(password.length < 6){
            binding.passwordInput.setError("Minimum 6 characters")
            return;
        }
        if(password != confirmpassword){
            binding.confirmPasswordInput.setError("Password not matching!")
            return;
        }

        //If everything is fine
        signupWithFirebase(email, password)

    }

    //把注册信息发给firebase验证
    //首先进入progress，然后无论成功都恢复ui，成功发送验证邮件，失败显示错误信息
    fun signupWithFirebase(email: String, password: String) {
        setInProgress(true)

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                it.user?.let { user ->
                    val userModel = UserModel(user.uid, email, email.substringBefore("@"),"https://firebasestorage.googleapis.com/v0/b/tiktok-6be6e.appspot.com/o/avatars%2Fdefault_avatar.jpg?alt=media")
                    Firebase.firestore.collection("users")
                        .document(user.uid)
                        .set(userModel)
                        .addOnSuccessListener {
                            Log.d("DEBUG", "You have created your account successfully!，Writing into Firestore...")
                            UiUtil.showToast(applicationContext, "Account Created Successfully.")
                            setInProgress(false)
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.e("DEBUG", "写入 Firestore 失败: ${e.localizedMessage}")
                            UiUtil.showToast(applicationContext, e.localizedMessage ?: "Firestore 写入失败")
                            setInProgress(false)
                        }
                }
            }
            .addOnFailureListener {
                Log.e("DEBUG", "注册失败: ${it.localizedMessage}")
                UiUtil.showToast(applicationContext, it.localizedMessage ?: "注册失败")
                setInProgress(false)
            }
    }



}



