package com.example.tiktok

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsCompat
import com.example.tiktok.Util.UiUtil
import com.example.tiktok.databinding.ActivityMainBinding

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
                UiUtil.showToast(this, "Got Image" + selectedImgUri.toString())
            }

        }
        enableEdgeToEdge()
        setContentView(binding.root)
        //按钮绑定
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
