package com.d10ng.datastore.example

import com.d10ng.datastore.annotation.PreferenceDataStore
import com.d10ng.datastore.annotation.PreferenceKey

/**
 *
 * @Author d10ng
 * @Date 2023/11/15 14:41
 */
@PreferenceDataStore("temp")
interface TempData {

    @PreferenceKey
    val key1: String

    @PreferenceKey([Long::class])
    val key2: Int
}