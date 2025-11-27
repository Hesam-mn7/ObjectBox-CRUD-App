package com.example.myapplication.data

import android.content.Context
import android.util.Log
import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.android.AndroidObjectBrowser

object ObjectBox {

    lateinit var boxStore: BoxStore
        private set

    fun init(context: Context) {
        boxStore = MyObjectBox.builder()
            .androidContext(context.applicationContext)
            .build()

        // 🔥 این بخش برای فعال کردن ObjectBox Browser
        val started = AndroidObjectBrowser(boxStore).start(context.applicationContext)
        Log.d("ObjectBox", "Browser started? $started")
//        if (started) {
//            Log.d("ObjectBox", "ObjectBrowser started — check logcat for URL")
//        } else {
//            Log.d("ObjectBox", "ObjectBrowser could NOT start")
//        }
    }

    fun <T> box(clazz: Class<T>): Box<T> {
        return boxStore.boxFor(clazz)
    }
}
