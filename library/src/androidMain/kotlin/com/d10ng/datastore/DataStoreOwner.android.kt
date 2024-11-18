package com.d10ng.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

internal actual fun createDataStoreByName(name: String): DataStore<Preferences> = createDataStore(
    producePath = { ctx.filesDir.resolve(name).absolutePath }
)