package com.example.tiktok

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.content.pm.PackageManager
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.tiktok.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    // for image/video picker
    private var selectedImgUri: Uri? = null
    lateinit var imgLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ 初始化图片/视频选择器
        imgLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                selectedImgUri = result.data?.data
                selectedImgUri?.let {
                    val intent = Intent(this, UploadActivity::class.java)
                    intent.putExtra("imageUri", it.toString())
                    startActivity(intent)
                }
            }
        }

        // ✅ 默认加载帖子首页 Fragment
        replaceFragment(PostFragment())

        // ✅ 底部导航栏点击事件
        binding.navHome.setOnClickListener {
            replaceFragment(PostFragment())
        }

        // binding.navMarket.setOnClickListener {
        //     replaceFragment(MarketFragment())
        // }

        binding.navMsg.setOnClickListener {
            replaceFragment(ChatListFragment())
        }

        // binding.navProfile.setOnClickListener {
        //     replaceFragment(ProfileFragment())
        // }

        // ✅ 中间的 + 发帖按钮
        binding.navAdd.setOnClickListener {
            val bottomSheetView = layoutInflater.inflate(R.layout.bottom_sheet, null)
            val bottomSheetDialog = com.google.android.material.bottomsheet.BottomSheetDialog(this)
            bottomSheetDialog.setContentView(bottomSheetView)

            val chooseGallery = bottomSheetView.findViewById<TextView>(R.id.choose_gallery)
            val chooseCamera = bottomSheetView.findViewById<TextView>(R.id.choose_camera)

            chooseGallery.setOnClickListener {
                bottomSheetDialog.dismiss()
                checkPermissionAndOpenGallery()
            }

            chooseCamera.setOnClickListener {
                bottomSheetDialog.dismiss()
                openCamera()
            }

            bottomSheetDialog.show()
        }
    }

    // 🔧 fragment 切换函数
    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainer.id, fragment)
            .commit()
    }

    // 🔧 相机打开函数（可填充逻辑）
    fun openCamera() {
        // TODO: Add camera intent if needed
    }

    // 🔧 相册打开函数
    fun openAlbum() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*", "video/*"))
        imgLauncher.launch(intent)
    }

    // 🔧 权限检查
    fun checkPermissionAndOpenGallery() {
        val readImgPermission: String
        val readVideoPermission: String

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            readImgPermission = android.Manifest.permission.READ_MEDIA_IMAGES
            readVideoPermission = android.Manifest.permission.READ_MEDIA_VIDEO
        } else {
            readImgPermission = android.Manifest.permission.READ_EXTERNAL_STORAGE
            readVideoPermission = android.Manifest.permission.READ_EXTERNAL_STORAGE
        }

        if (ContextCompat.checkSelfPermission(this, readImgPermission) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, readVideoPermission) == PackageManager.PERMISSION_GRANTED) {
            openAlbum()
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(readImgPermission, readVideoPermission), 100)
        }
    }
}
