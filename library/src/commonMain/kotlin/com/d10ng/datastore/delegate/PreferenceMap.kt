package com.d10ng.datastore.delegate

/**
 * Interface for accessing dynamic keys.
 * Usages:
 * val scores by mapPreference<String, Int>(default = 0)
 * scores["user_1"].set(100)
 */
interface PreferenceMap<K, V> {
    operator fun get(key: K): PreferenceItem<V>
}
