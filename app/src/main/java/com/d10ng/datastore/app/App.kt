package com.d10ng.datastore.app

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

class App: Application() {

    companion object {
        lateinit var instance: App
        lateinit var spf: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        spf = getSharedPreferences("settings", Context.MODE_PRIVATE)
    }
}