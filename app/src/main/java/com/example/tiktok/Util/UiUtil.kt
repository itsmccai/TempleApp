package com.example.tiktok.Util

import android.content.Context
import android.widget.Toast

//used to print the text
object UiUtil {
    fun showToast(context:Context, message:String)
    {
        Toast.makeText(context, message,Toast.LENGTH_LONG).show()
    }


}