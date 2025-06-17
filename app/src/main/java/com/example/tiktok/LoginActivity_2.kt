package com.example.tiktok

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tiktok.Util.UiUtil
import com.example.tiktok.databinding.ActivityLogin2Binding
import com.google.firebase.auth.FirebaseAuth
import io.grpc.okhttp.internal.Util

class LoginActivity_2 : AppCompatActivity() {

    //lateinit： 晚点赋值类型
    lateinit var binding: ActivityLogin2Binding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        //通过layoutinfalter将布局xml文件转化成一个绑定对象
        binding = ActivityLogin2Binding.inflate(layoutInflater)
        //将这个布局显示在页面上：可以替换viewbyid的写法
        setContentView(binding.root)

        //如果已经登录了 那么就保持登陆
        FirebaseAuth.getInstance().currentUser?.let {
            startActivity(Intent(this, MainActivity::class.java))
            finish()

        }


        //点击登录按钮
        binding.sumbitBtn.setOnClickListener{
            login()
        }
        //点击下方文字跳转 注册页面
        //然后 finish（） 结束当前activity
        binding.goToSignupBtn.setOnClickListener{
            startActivity(Intent(this, LoginActicity::class.java))
            finish()
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
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

    fun login()
    {
        val email = binding.emailInput.text.toString()
        val password = binding.passwordInput.text.toString()

        //Prerequisites
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches() || !email.endsWith("@temple.edu")){
            binding.emailInput.setError("Please use valid temple account!")
            return;
        }
        if(password.length < 6){
            binding.passwordInput.setError("Minimum 6 characters")
            return;
        }
        //If everything is fine
        loginWithFirebase(email, password)
    }

    fun loginWithFirebase(email:String, password:String)
    {
        setInProgress(true)
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email,password).addOnSuccessListener{
            UiUtil.showToast(this,"Login Successfully")
            setInProgress(false)
            startActivity(Intent(this, MainActivity::class.java))
            finish()}.addOnFailureListener{
            UiUtil.showToast(this,"Something went wrong...")
            setInProgress(false)

        }
    }

}