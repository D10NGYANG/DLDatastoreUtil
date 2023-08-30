package com.d10ng.datastore

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore

open class DataStoreOwner(name: String) : IDataStoreOwner {
    private val Context.dataStore by preferencesDataStore(name)
    override val dataStore: DataStore<Preferences> get() = context.dataStore
}

interface IDataStoreOwner {
    val context: Context get() = application
    val dataStore: DataStore<Preferences>

    companion object {
        internal lateinit var application: Application
    }
}