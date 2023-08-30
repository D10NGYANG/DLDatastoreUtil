package com.d10ng.datastore.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.d10ng.datastore.app.constant.SexType
import com.d10ng.datastore.app.ui.theme.DLDatastoreUtilTheme
import kotlin.time.TimeSource

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DLDatastoreUtilTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val timeSource = TimeSource.Monotonic

        // 测试spf写入耗时
        val start3 = timeSource.markNow()
        App.spf.edit().putString("config1", "text ${System.currentTimeMillis()}").apply()
        val end3 = timeSource.markNow()
        println("spf write cost: ${end3-start3}")
        // 测试spf读取耗时
        val start4 = timeSource.markNow()
        val config2 = App.spf.getString("config1", "")
        val end4 = timeSource.markNow()
        println("config2: $config2")
        println("spf read cost: ${end4-start4}")

        // 测试dataStore写入耗时
        val start = timeSource.markNow()
        AppDataStore.setConfig1Sync("text ${System.currentTimeMillis()}")
        val end = timeSource.markNow()
        println("datastore write cost: ${end-start}")
        // 测试dataStore读取耗时
        val start2 = timeSource.markNow()
        val config1 = AppDataStore.getConfig1Sync()
        val end2 = timeSource.markNow()
        println("config1: $config1")
        println("datastore read cost: ${end2-start2}")

        AppDataStore.setSexSync("A", SexType.MAN)
        println("read success: ${AppDataStore.getSexSync("A") == SexType.MAN}")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DLDatastoreUtilTheme {
        Greeting("Android")
    }
}