package com.d10ng.datastore.delegate

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.d10ng.datastore.DataStoreOwner
import com.d10ng.datastore.logPreferenceRead
import com.d10ng.datastore.logPreferenceWrite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

open class BaseDataStore(name: String) : DataStoreOwner(name) {

    // --- Delegate Providers ---

    protected fun intPreference(default: Int = 0, name: String? = null) =
        delegate(name, default) { intPreferencesKey(it) }

    protected fun doublePreference(default: Double = 0.0, name: String? = null) =
        delegate(name, default) { doublePreferencesKey(it) }

    protected fun longPreference(default: Long = 0L, name: String? = null) =
        delegate(name, default) { longPreferencesKey(it) }

    protected fun floatPreference(default: Float = 0f, name: String? = null) =
        delegate(name, default) { floatPreferencesKey(it) }

    protected fun booleanPreference(default: Boolean = false, name: String? = null) =
        delegate(name, default) { booleanPreferencesKey(it) }

    protected fun stringPreference(default: String = "", name: String? = null) =
        delegate(name, default) { stringPreferencesKey(it) }

    protected fun stringSetPreference(default: Set<String> = emptySet(), name: String? = null) =
        delegate(name, default) { stringSetPreferencesKey(it) }

    protected inline fun <reified T : Any> objectPreference(default: T, name: String? = null): ReadOnlyProperty<BaseDataStore, PreferenceItem<T>> {
        val serializer = serializer<T>()
        return objectDelegate(name, default, serializer)
    }

    protected inline fun <reified T : Enum<T>> enumPreference(default: T, name: String? = null): ReadOnlyProperty<BaseDataStore, PreferenceItem<T>> {
        return enumDelegate(name, default, enumValues<T>())
    }

    protected inline fun <reified K : Any, reified V : Any> mapPreference(default: V, name: String? = null): ReadOnlyProperty<BaseDataStore, PreferenceMap<K, V>> {
        val serializer = if (serializer<V>().descriptor.kind.toString() == "CLASS") serializer<V>() else null
        return mapDelegate(name, default, serializer)
    }

    // --- Implementation Helpers ---

    @PublishedApi
    internal fun <K, V> mapDelegate(
        name: String?,
        default: V,
        serializer: KSerializer<V>?
    ): ReadOnlyProperty<BaseDataStore, PreferenceMap<K, V>> {
        return object : ReadOnlyProperty<BaseDataStore, PreferenceMap<K, V>> {
            private var item: PreferenceMap<K, V>? = null
            override fun getValue(thisRef: BaseDataStore, property: KProperty<*>): PreferenceMap<K, V> {
                return item ?: run {
                    val keyName = name ?: property.name
                    PreferenceMapImpl<K, V>(thisRef.dataStore, keyName, default, serializer, thisRef.json).also { item = it }
                }
            }
        }
    }

    @PublishedApi
    internal fun <T> delegate(
        name: String?,
        default: T,
        keyFactory: (String) -> Preferences.Key<T>
    ): ReadOnlyProperty<BaseDataStore, PreferenceItem<T>> {
        return object : ReadOnlyProperty<BaseDataStore, PreferenceItem<T>> {
            private var item: PreferenceItem<T>? = null
            override fun getValue(thisRef: BaseDataStore, property: KProperty<*>): PreferenceItem<T> {
                return item ?: run {
                    val keyName = name ?: property.name
                    val key = keyFactory(keyName)
                    PreferenceItemImpl(thisRef.dataStore, key, default).also { item = it }
                }
            }
        }
    }

    @PublishedApi
    internal fun <T : Any> objectDelegate(
        name: String?,
        default: T,
        serializer: KSerializer<T>
    ): ReadOnlyProperty<BaseDataStore, PreferenceItem<T>> {
        return object : ReadOnlyProperty<BaseDataStore, PreferenceItem<T>> {
            private var item: PreferenceItem<T>? = null
            override fun getValue(thisRef: BaseDataStore, property: KProperty<*>): PreferenceItem<T> {
                return item ?: run {
                    val keyName = name ?: property.name
                    val key = stringPreferencesKey(keyName)
                    ObjectPreferenceItemImpl(thisRef.dataStore, key, default, serializer, thisRef.json).also { item = it }
                }
            }
        }
    }

    @PublishedApi
    internal fun <T : Enum<T>> enumDelegate(
        name: String?,
        default: T,
        enumValues: Array<T>
    ): ReadOnlyProperty<BaseDataStore, PreferenceItem<T>> {
        return object : ReadOnlyProperty<BaseDataStore, PreferenceItem<T>> {
            private var item: PreferenceItem<T>? = null
            override fun getValue(thisRef: BaseDataStore, property: KProperty<*>): PreferenceItem<T> {
                return item ?: run {
                    val keyName = name ?: property.name
                    val key = stringPreferencesKey(keyName)
                    EnumPreferenceItemImpl(thisRef.dataStore, key, default, enumValues).also { item = it }
                }
            }
        }
    }

    // --- Private Implementations ---

    private class PreferenceItemImpl<T>(
        private val dataStore: DataStore<Preferences>,
        private val key: Preferences.Key<T>,
        private val default: T
    ) : PreferenceItem<T> {
        override val flow: Flow<T> = dataStore.data.map { it[key] ?: default }
        override suspend fun get(): T {
            val pref = dataStore.data.first()
            val isDefault = !pref.contains(key)
            val value = pref[key] ?: default
            logPreferenceRead(key.name, value, isDefault)
            return value
        }
        override fun getSync(): T = runBlocking { get() }
        override suspend fun set(value: T) {
            dataStore.edit { it[key] = value }
            logPreferenceWrite(key.name, value)
        }
        override fun setSync(value: T) = runBlocking { set(value) }
    }

    private class ObjectPreferenceItemImpl<T>(
        private val dataStore: DataStore<Preferences>,
        private val key: Preferences.Key<String>,
        private val default: T,
        private val serializer: KSerializer<T>,
        private val json: Json
    ) : PreferenceItem<T> {
        override val flow: Flow<T> = dataStore.data.map { pref ->
            pref[key]?.let { json.decodeFromString(serializer, it) } ?: default
        }
        override suspend fun get(): T {
            val pref = dataStore.data.first()
            val stored = pref[key]
            val value = stored?.let { json.decodeFromString(serializer, it) } ?: default
            logPreferenceRead(key.name, value, stored == null)
            return value
        }
        override fun getSync(): T = runBlocking { get() }
        override suspend fun set(value: T) {
            val jsonStr = json.encodeToString(serializer, value)
            dataStore.edit { it[key] = jsonStr }
            logPreferenceWrite(key.name, value)
        }
        override fun setSync(value: T) = runBlocking { set(value) }
    }

    private class EnumPreferenceItemImpl<T : Enum<T>>(
        private val dataStore: DataStore<Preferences>,
        private val key: Preferences.Key<String>,
        private val default: T,
        private val enumValues: Array<T>
    ) : PreferenceItem<T> {
        override val flow: Flow<T> = dataStore.data.map { pref ->
            pref[key]?.let { name -> enumValues.find { it.name == name } } ?: default
        }
        override suspend fun get(): T {
            val pref = dataStore.data.first()
            val stored = pref[key]
            val value = stored?.let { name -> enumValues.find { it.name == name } } ?: default
            logPreferenceRead(key.name, value, stored == null)
            return value
        }
        override fun getSync(): T = runBlocking { get() }
        override suspend fun set(value: T) {
            dataStore.edit { it[key] = value.name }
            logPreferenceWrite(key.name, value)
        }
        override fun setSync(value: T) = runBlocking { set(value) }
    }

    private class PreferenceMapImpl<K, V>(
        private val dataStore: DataStore<Preferences>,
        private val prefix: String,
        private val default: V,
        private val serializer: KSerializer<V>?,
        private val json: Json
    ) : PreferenceMap<K, V> {
        override fun get(key: K): PreferenceItem<V> {
            val fullKeyName = "$prefix:$key"
            val prefKey: Preferences.Key<*> = when (default) {
                is Int -> intPreferencesKey(fullKeyName)
                is Double -> doublePreferencesKey(fullKeyName)
                is Long -> longPreferencesKey(fullKeyName)
                is Float -> floatPreferencesKey(fullKeyName)
                is Boolean -> booleanPreferencesKey(fullKeyName)
                is String -> stringPreferencesKey(fullKeyName)
                is Set<*> -> stringSetPreferencesKey(fullKeyName)
                else -> stringPreferencesKey(fullKeyName)
            }
            
            return if (serializer != null) {
                // Object type
                ObjectPreferenceItemImpl(dataStore, stringPreferencesKey(fullKeyName), default, serializer, json)
            } else {
                // Basic type
                @Suppress("UNCHECKED_CAST")
                PreferenceItemImpl(dataStore, prefKey as Preferences.Key<V>, default)
            }
        }
    }
}
