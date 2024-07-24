package com.example.app

import android.app.Application
import com.alibaba.android.arouter.launcher.ARouter

class ExampleApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ARouter.openLog()
        ARouter.openDebug()
        ARouter.init(this)
    }
}