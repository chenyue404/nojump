package com.chenyue404.nojump

import android.app.Application
import android.content.Context

class App : Application() {
    companion object {
        lateinit var gContext: App
    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        gContext = this
    }
}