package com.example.tiktok

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tiktok.Model.PostModel
import com.example.tiktok.Util.UiUtil
import com.example.tiktok.databinding.ActivityMainBinding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class MainActivity : AppCompatActivity() {
    lateinit var binding:ActivityMainBinding
    //for imgPicker
    private var selectedImgUri: Uri? = null
    lateinit var imgLauncher:ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        //用来启动另一个 Activity 并获取其返回结果
        imgLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){result->
            if(result.resultCode == RESULT_OK)
            {
                selectedImgUri = result.data?.data
                if (selectedImgUri != null) {
                    val intent = Intent(this, UploadActivity::class.java)
                    intent.putExtra("imageUri", selectedImgUri.toString())  // to postPage
                    startActivity(intent)
                }
            }

        }
        enableEdgeToEdge()
        setContentView(binding.root)
        //post信息区域
        val samplePosts = listOf(
            PostModel(R.drawable.logo, "这是第一条内容，我么一起回家吧"),
            PostModel(R.drawable.logo, "这是第二条内容"),
            PostModel(R.drawable.logo, "这是第三条内容,原来你也是我哇哦特曼我我我哦我我我我我我我我我我我我我"),
            PostModel(R.drawable.logo, "这是第二条内容"),
            PostModel(R.drawable.logo, "这是第二条内容"),
            PostModel(R.drawable.logo, "这是第二条内容"),
            PostModel(R.drawable.logo, "这是第二条内容"),
            PostModel(R.drawable.logo, "这是第二条内容"),

            )

        // 设置 RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.postRecyclerView)
        //recyclerView.layoutManager = LinearLayoutManager(this)
        //切换成两列瀑布
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.adapter = PostAdapter(samplePosts)

        //按钮绑定
        //1.添加nav按钮
        binding.navAdd.setOnClickListener{
            val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet, null)
            val bottomSheetDialog = com.google.android.material.bottomsheet.BottomSheetDialog(this)
            bottomSheetDialog.setContentView(bottomSheetView)

            val chooseGallery = bottomSheetView.findViewById<TextView>(R.id.choose_gallery)
            val chooseCamera = bottomSheetView.findViewById<TextView>(R.id.choose_camera)

            chooseGallery.setOnClickListener {
                bottomSheetDialog.dismiss()
                //ask for premission if dont have
                checkPermissionAndOpenGallery()

                //
                //startActivity(Intent(this, UploadActivity::class.java))
            }
            chooseCamera.setOnClickListener {
                bottomSheetDialog.dismiss()
                openCamera()
            }
            bottomSheetDialog.show()
        }
        //2. 退出按钮
        val logoutBtn = findViewById<Button>(R.id.btnLogout)
        logoutBtn.setOnClickListener{
            //todo：清除登陆信息
            //
            startActivity(Intent(this, LoginActicity::class.java))
            finish()
        }



        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
    //functions
    fun openCamera(){}
    fun openAlbum()
    {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"  //allows any types
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        //intent.addCategory(Intent.CATEGORY_OPENABLE)
        imgLauncher.launch(intent)

    }
    fun checkPermissionAndOpenGallery()
    {
        var readImgPermission = ""
        var readVideoPermission = ""

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        {
            // Android 13+
            readImgPermission = android.Manifest.permission.READ_MEDIA_IMAGES
            readVideoPermission = android.Manifest.permission.READ_MEDIA_VIDEO}
        else
        {
            readImgPermission = android.Manifest.permission.READ_EXTERNAL_STORAGE
            readVideoPermission = android.Manifest.permission.READ_EXTERNAL_STORAGE

        }
        if (ContextCompat.checkSelfPermission(this, readImgPermission) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, readVideoPermission) == PackageManager.PERMISSION_GRANTED)
        {
            openAlbum()
        }
        else
        {
           ActivityCompat.requestPermissions(this, arrayOf(readImgPermission,readVideoPermission),100)
        }
    }

    }
