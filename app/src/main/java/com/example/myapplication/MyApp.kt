package com.example.myapplication

import android.app.Application
import com.example.myapplication.data.ObjectBox

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        ObjectBox.init(this)
    }
}