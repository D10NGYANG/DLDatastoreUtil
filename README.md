# DLDatastoreUtil

jetpack datastore 封装工具，减少模版代码，确保类型安全，避免类型或者键名不一致导致的异常；

*最新版本`0.0.7`*

## 参考
- [DylanCaiCoding/DataStoreKTX](https://github.com/DylanCaiCoding/DataStoreKTX)

# 特性
- [x] 自动生成模版代码
- [x] 支持带参数的key
- [x] 支持同步与异步方法
- [x] 支持自定义datastore名称
- [x] 支持枚举类型数据
- [x] 支持基于kotlin.serialization的序列化data class类型数据
- [x] 支持设置默认值 **暂时只能以字符串的形式去设置默认值，因为KSP本身还不支持去读取参数的默认值**

## 安装说明
1 添加Maven仓库，打开项目根目录`settings.gradle.kts`文件添加以下内容：
```kts
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://raw.githubusercontent.com/D10NGYANG/maven-repo/main/repository")
    }
}
```

2 添加`ksp`插件，打开项目根目录`build.gradle.kts`文件添加以下内容，`ksp_ver`为ksp版本，最新版本请查阅[google/ksp](https://github.com/google/ksp/releases)：
```kts
plugins {
    id("com.google.devtools.ksp") version ksp_ver apply false
    // 可选，如果需要支持data class类型数据
    id("org.jetbrains.kotlin.plugin.serialization") version kotlin_ver apply false
}
```
打开项目模块（一般为`app`）目录下的`build.gradle.kts`文件添加以下内容：
```kts
plugins {
    id("com.google.devtools.ksp")
    // 可选，如果需要支持data class类型数据
    id("org.jetbrains.kotlin.plugin.serialization")
}
```

3 添加依赖
```kts
dependencies {
    // jetpack datastore 封装工具
    implementation("com.github.D10NGYANG:DLDatastoreUtil:$ver")
    ksp("com.github.D10NGYANG:DLDatastoreUtil-Processor:$ver")
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    // kotlinx.serialization 可选，如果需要支持data class类型数据
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.2")
}
```

4 混淆
```properties
-keep class com.d10ng.datastore.** {*;}
-dontwarn com.d10ng.datastore.**
```

## 使用说明
1 创建一个`datastore`，例如：
```kotlin
// 创建一个名为settings的datastore，name参数可以省略，默认为类名
// 该类会自动生成一个SettingsDataStore类，用于操作datastore
@PreferenceDataStore(name = "settings")
interface SettingData
```

2 创建一个`datastore`的`key`，例如：
```kotlin
@PreferenceDataStore(name = "settings")
interface SettingData {
    // 创建一个名为username的key
    @PreferenceKey
    val username: String
}
```
自动生成模版代码：
```kotlin
open class SettingDataStore : DataStoreOwner("settings") {
    
    companion object {
        val instance by lazy { SettingDataStore() }
    }

    // -------------- 读取 --------------
    // 获取key为username的值Flow
    open fun getUsernameFlow() = dataStore.data.map { it[stringPreferencesKey("username")] }
    // 异步获取key为username的值
    open suspend fun getUsername() = getUsernameFlow().first()
    // 同步获取key为username的值
    open fun getUsernameSync() = runBlocking { getUsername() }

    // -------------- 写入 --------------
    // 异步设置key为username的值
    open suspend fun setUsername(value: String) =
        dataStore.edit { it[stringPreferencesKey("username")] = value }
    // 同步设置key为username的值
    open fun setUsernameSync(value: String) = runBlocking { setUsername(value) }
}
```
> 注意：
> - `datastore`的`key`必须是`val`类型，且必须有`@PreferenceKey`注解；
> - `datastore`的`key`的类型必须是`String`、`Int`、`Long`、`Float`、`Double`、`Boolean`、`Set<String>`、`Enum`、`data class`类型；
> - Enum枚举类型的数据解析与反解析是根据枚举的`name`属性来进行的，所以不要轻易修改枚举值；
> - data class类型的数据解析与反解析是基于kotlin.serialization的，所以需要在data class类上添加`@Serializable`注解，且不要轻易修改data class的属性名；
> - 除非别无选择，否则不建议使用同步方法，会阻塞主线程，耗时会比MMKV与传统的Spf要长，优先使用异步方法；

3 创建带参数的`datastore`的`key`，例如：
```kotlin
// 生成的key为：username:${key0}:${key1}
@PreferenceKey(keys = [String::class, Int::class])
val allowOpen: Boolean
```
自动生成模版代码：
```kotlin
open fun getAllowOpenFlow(key0: String, key1: Int) = dataStore.data.map { it[booleanPreferencesKey("allowOpen:${key0}:${key1}")] }
open suspend fun getAllowOpen(key0: String, key1: Int) = getAllowOpenFlow(key0, key1).first()
open fun getAllowOpenSync(key0: String, key1: Int) = runBlocking { getAllowOpen(key0, key1) }
open suspend fun setAllowOpen(key0: String, key1: Int, value: Boolean) = dataStore.edit { it[booleanPreferencesKey("allowOpen:${key0}:${key1}")] = value }
open fun setAllowOpenSync(key0: String, key1: Int, value: Boolean) = runBlocking { setAllowOpen(key0, key1, value) }
```
> 这种主要作用在于，假设一种数据是属于不同用户的，那么可以使用这种方式来区分不同用户的数据，例如：allowOpen:${userId}:${funId}

4 设置默认值
```kotlin
@PreferenceKey(default = "\"d10ng\"")
val username: String

@PreferenceKey(default = "123456")
val age: Int
```
> 这种方式就是将您所输入的字符串作为模版拼接到生成的代码中，是一个取巧的方式，待后续KSP支持读取参数值后可能会弃用；