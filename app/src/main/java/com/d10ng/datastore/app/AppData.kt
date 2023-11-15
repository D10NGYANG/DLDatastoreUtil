package com.d10ng.datastore.app

import androidx.datastore.preferences.core.*
import com.d10ng.datastore.DataStoreOwner
import com.d10ng.datastore.app.data.Person
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString

open class AppDataStore: DataStoreOwner("appdata") {

    companion object {
        val instant by lazy { AppDataStore() }
    }

    fun getConfig1Flow() = dataStore.data.map { it[stringPreferencesKey("config1")] }
    suspend fun getConfig1() = getConfig1Flow().first()
    fun getConfig1Sync() = runBlocking { getConfig1() }
    suspend fun setConfig1(value: String) = dataStore.edit { it[stringPreferencesKey("config1")] = value }
    fun setConfig1Sync(value: String) = runBlocking { setConfig1(value) }

    fun getConfig2Flow(key1: Int) = dataStore.data.map { it[stringPreferencesKey("config2:${key1}")] }
    suspend fun getConfig2(key1: Int) = getConfig2Flow(key1).first()
    fun getConfig2Sync(key1: Int) = runBlocking { getConfig2(key1) }
    suspend fun setConfig2(key1: Int, value: String) = dataStore.edit { it[stringPreferencesKey("config2:${key1}")] = value }
    fun setConfig2Sync(key1: Int, value: String) = runBlocking { setConfig2(key1, value) }

    fun getSexFlow(key0: String) = dataStore.data.map { it[stringPreferencesKey("sex:${key0}")]?.let { d -> com.d10ng.datastore.app.constant.SexType.valueOf(d) } }
    suspend fun getSex(key0: String) = getSexFlow(key0).first()
    fun getSexSync(key0: String) = runBlocking { getSex(key0) }
    suspend fun setSex(key0: String, value: com.d10ng.datastore.app.constant.SexType) = dataStore.edit { it[stringPreferencesKey("sex:${key0}")] = value.toString() }
    fun setSexSync(key0: String, value: com.d10ng.datastore.app.constant.SexType) = runBlocking { setSex(key0, value) }

    fun getPersonFlow(key0: String) = dataStore.data.map { it[stringPreferencesKey("person:${key0}")]?.let { s -> json.decodeFromString<Person>(s) } }

    suspend fun setPerson(key0: String, value: Person) = dataStore.edit { it[stringPreferencesKey("person:${key0}")] = json.encodeToString(value) }
}