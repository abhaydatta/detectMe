package com.passbasedemo.detectme

import android.app.Application

class DetectMeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object{
        lateinit var instance:DetectMeApplication
    }
}