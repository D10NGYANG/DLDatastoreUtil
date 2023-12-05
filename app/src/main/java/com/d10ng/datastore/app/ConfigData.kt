package com.d10ng.datastore.app

import com.d10ng.datastore.annotation.PreferenceDataStore
import com.d10ng.datastore.annotation.PreferenceKey
import com.d10ng.datastore.app.data.Person

/**
 *
 * @Author d10ng
 * @Date 2023/11/15 11:27
 */
@PreferenceDataStore("config")
interface ConfigData {

    @PreferenceKey()
    val person: Person

    @PreferenceKey(default = "18")
    val age: Int

    @PreferenceKey
    val height: Float
}