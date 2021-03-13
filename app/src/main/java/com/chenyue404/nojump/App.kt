package com.chenyue404.nojump

import android.app.Application
import android.content.Context

class App : Application() {
    companion object {
        lateinit var gContext: App
        val TAG = "nojump--app-"
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        gContext = this
    }
}