package com.d10ng.datastore

import android.app.Application
import android.content.Context
import androidx.startup.Initializer

class DataStoreInitializer: Initializer<Unit> {

    override fun create(context: Context) {
        IDataStoreOwner.application = context as Application
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return mutableListOf()
    }
}