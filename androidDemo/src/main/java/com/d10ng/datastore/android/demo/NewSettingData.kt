package com.d10ng.datastore.android.demo

import com.d10ng.datastore.android.demo.constant.SexType
import com.d10ng.datastore.android.demo.data.Person
import com.d10ng.datastore.delegate.BaseDataStore

object NewSettingData : BaseDataStore("new_settings") {

    val username by stringPreference("d10ng")

    val age by intPreference(18)

    val sex by enumPreference(SexType.MAN)

    val person by objectPreference(Person(name = "default", age = 0, height = 0f, sex = SexType.MAN))

    // Dynamic key example: scores["level_1"].set(100)
    val scores by mapPreference<String, Int>(0)
    
    // Dynamic key example: userConfigs["user_id_1"].set(Person(...))
    val userConfigs by mapPreference<String, Person>(Person(name = "default", age = 0, height = 0f, sex = SexType.MAN))
}
