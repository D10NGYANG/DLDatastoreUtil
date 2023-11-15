package com.d10ng.datastore.app

import com.d10ng.datastore.annotation.PreferenceDataStore
import com.d10ng.datastore.annotation.PreferenceKey
import com.d10ng.datastore.app.constant.SexType
import com.d10ng.datastore.app.data.Person

@PreferenceDataStore(name = "settings")
interface SettingData {

    @PreferenceKey
    val username: String
        get() = "admin"

    @PreferenceKey
    val password: String

    @PreferenceKey(keys = [String::class])
    val phone: String

    @PreferenceKey(keys = [String::class])
    val age: Int

    @PreferenceKey(keys = [String::class])
    val createTimestamp: Long

    @PreferenceKey(keys = [String::class])
    val height: Float

    @PreferenceKey(keys = [String::class])
    val weight: Double

    @PreferenceKey(keys = [String::class])
    val sex: SexType

    @PreferenceKey(keys = [String::class, Int::class])
    val allowOpen: Boolean

    @PreferenceKey(keys = [String::class])
    val things: Set<String>

    @PreferenceKey(keys = [String::class])
    val person: Person
}