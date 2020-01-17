package com.yiqi.muying

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.view.View
import android.widget.Checkable
import android.widget.Toast


var <T : View> T.lastClickTime: Long
    set(value) = setTag(1766613352, value)
    get() = getTag(1766613352) as? Long ?: 0

inline fun <T : View> T.onClick(time: Long = 800, crossinline block: (T) -> Unit) {
    setOnClickListener {
        val currentTimeMillis = System.currentTimeMillis()
        if (currentTimeMillis - lastClickTime > time || this is Checkable) {
            lastClickTime = currentTimeMillis
            block(this)
        }
    }
}

fun Activity.toast(text:String){
    Toast.makeText(applicationContext,text,Toast.LENGTH_SHORT).show()
}

fun Activity.putStringSp(key:String,value:String){
    val editor= getSharedPreferences("userInfo", MODE_PRIVATE).edit()
    editor.putString(key, value)
    editor.apply()
}

fun Activity.getStringSp(key:String):String{
    val sp = getSharedPreferences("userInfo", MODE_PRIVATE)
    return sp.getString(key, "")!!
}