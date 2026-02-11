package com.d10ng.datastore.android.demo

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.d10ng.datastore.android.demo.constant.SexType
import com.d10ng.datastore.android.demo.data.Person
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.d10ng.datastore.android.demo.test", appContext.packageName)
    }

    @Test
    fun testNewSettingData() = runBlocking {
        // Initial value
        // Note: DataStore persists data, so we reset it or check whatever
        // Since we can't easily clear it without Context, we just set and check.
        
        NewSettingData.username.set("d10ng")
        assertEquals("d10ng", NewSettingData.username.get())

        // Update value
        NewSettingData.username.set("hello")
        assertEquals("hello", NewSettingData.username.get())

        // Enum
        NewSettingData.sex.set(SexType.MAN)
        assertEquals(SexType.MAN, NewSettingData.sex.get())
        NewSettingData.sex.set(SexType.WOMAN)
        assertEquals(SexType.WOMAN, NewSettingData.sex.get())

        // Object
        val p = Person(name = "Test", age = 20, height = 180f, sex = SexType.MAN)
        NewSettingData.person.set(p)
        assertEquals(p, NewSettingData.person.get())

        // Dynamic Keys
        NewSettingData.scores["level_1"].set(100)
        NewSettingData.scores["level_2"].set(200)
        assertEquals(100, NewSettingData.scores["level_1"].get())
        assertEquals(200, NewSettingData.scores["level_2"].get())

        // Dynamic Object Keys
        val p2 = Person(name = "User2", age = 25, height = 175f, sex = SexType.WOMAN)
        NewSettingData.userConfigs["user_2"].set(p2)
        assertEquals(p2, NewSettingData.userConfigs["user_2"].get())
    }
}
