package com.d10ng.datastore

import android.app.Application
import android.content.Context
import androidx.startup.Initializer

/**
 * 启动初始化
 * @Author d10ng
 * @Date 2023/9/1 13:51
 */
internal class StartupInitializer : Initializer<Unit> {

    companion object {
        lateinit var application: Application
    }

    override fun create(context: Context) {
        application = context as Application
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}

internal val ctx by lazy { StartupInitializer.application }