package com.passbasedemo.detectme.presenter.util

import android.content.Context
import android.content.SharedPreferences

object SharedPrefManager {
    private lateinit var prefs :SharedPreferences
    private const val PREF_NAME = "DetectMe"

    fun init(context: Context){
        prefs = context.getSharedPreferences(PREF_NAME,Context.MODE_PRIVATE)
    }

    fun read(key:String, value:Float):Float?{
        return prefs.getFloat(key,value)
    }

    fun write(key:String,value: Float){
        val prefsEditor:SharedPreferences.Editor = prefs.edit()
        with(prefsEditor){
            putFloat(key,value)
            commit()
        }

    }
}