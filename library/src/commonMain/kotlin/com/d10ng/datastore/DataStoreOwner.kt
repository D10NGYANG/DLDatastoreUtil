package com.d10ng.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import kotlinx.serialization.json.Json
import okio.Path.Companion.toPath

internal expect fun createDataStoreByName(name: String): DataStore<Preferences>

internal fun createDataStore(producePath: () -> String): DataStore<Preferences> =
    PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )


open class DataStoreOwner(name: String) : IDataStoreOwner {
    override val dataStore: DataStore<Preferences> by lazy {
        val fileName = if (name.endsWith(".preferences_pb")) name else "${name}.preferences_pb"
        createDataStoreByName(fileName)
    }

    val json = IDataStoreOwner.json
}

interface IDataStoreOwner {
    val dataStore: DataStore<Preferences>

    companion object {

        /** 自定义规则的JSON工具 */
        val json by lazy {
            Json {
                // 忽略JSON字符串里有但data class中没有的key
                ignoreUnknownKeys = true
                // 如果接收到的JSON字符串的value为null，但是data class中的对应属性不能为null，那就使用属性的默认值
                coerceInputValues = true
                // 如果创建data class实例时有些属性没有赋值，那就使用默认值进行转换成JSON字符串
                encodeDefaults = true
                // 属性放宽
                isLenient = true
            }
        }
    }
}