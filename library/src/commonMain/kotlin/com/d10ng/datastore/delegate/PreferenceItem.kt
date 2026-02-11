package com.d10ng.datastore.delegate

import kotlinx.coroutines.flow.Flow

interface PreferenceItem<T> {
    val flow: Flow<T>
    suspend fun get(): T
    fun getSync(): T
    suspend fun set(value: T)
    fun setSync(value: T)
}
