# DLDatastoreUtil

jetpack datastore 封装工具，减少模版代码，确保类型安全，避免类型或者键名不一致导致的异常；

*最新版本`0.3.0`*

> ⚠️ 从`0.1.0`开始，转换成`kotlin multiplatform`架构，支持Android、iOS；
>
> 🚀 **推荐使用 0.3.0+ 版本新增的“委托属性模式”，彻底告别 KSP 编译生成代码的烦恼！**

## 参考
- [DylanCaiCoding/DataStoreKTX](https://github.com/DylanCaiCoding/DataStoreKTX)

# 特性
- [x] **[New] 零编译延迟**：使用 Kotlin 委托属性，写完即用，无需等待 KSP 生成代码。
- [x] **[New] 动态 Key 支持**：支持类似 Map 的动态键值存取 (`mapPreference`)。
- [x] **[New] 完美 IDE 支持**：彻底解决接口文件被误判为无用代码的问题。
- [x] 自动生成模版代码 (旧版 KSP 模式)
- [x] 支持带参数的key
- [x] 支持同步与异步方法
- [x] 支持自定义datastore名称
- [x] 支持枚举类型数据
- [x] 支持基于kotlin.serialization的序列化data class类型数据
- [x] 支持设置默认值

## 安装说明

1、添加Maven仓库，打开项目根目录`settings.gradle.kts`文件添加以下内容：
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

2、添加依赖

2.1、Android项目
```kts
dependencies {
    // jetpack datastore 封装工具
    implementation("com.github.D10NGYANG:DLDatastoreUtil:$ver")
    
    // [可选] 如果继续使用旧版 KSP 模式，需要保留以下依赖：
    // ksp("com.github.D10NGYANG:DLDatastoreUtil-Processor:$ver")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
    // kotlinx.serialization 可选，如果需要支持data class类型数据
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
}
```

2.2、Compose Multiplatform 项目
```kts
kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation("com.github.D10NGYANG:DLDatastoreUtil:$ver")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.9.0")
            }
        }
        androidMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2")
        }
    }
}
// [可选] 如果继续使用旧版 KSP 模式，需保留 kspCommonMainMetadata 配置
```

3、混淆
```properties
-keep class com.d10ng.datastore.** {*;}
-dontwarn com.d10ng.datastore.**
```

---

# 🚀 0.3.0 新版使用指南 (推荐)

从 0.3.0 版本开始，我们引入了基于 **Kotlin 委托属性 (Delegated Properties)** 的新方案，全面解决旧版 KSP 模式的痛点。

1、核心功能与优势

| 特性 | 旧版 (KSP 模式) | 新版 (委托模式) | 优势 |
| :--- | :--- | :--- | :--- |
| **生效时间** | 需执行 `ksp` 任务生成代码 | **即时生效** | 修改 Key 后无需编译，开发效率极大提升 |
| **IDE 支持** | 定义接口常被误判为未使用 | **完美支持** | 定义即调用，IDE 引用关系清晰 |
| **动态 Key** | 通过 `@PreferenceKey(keys=...)` 支持 | **`mapPreference`** | 类似 `Map` 操作，更符合直觉 |
| **配置复杂度** | 需配置 KSP 插件和路径 | **零配置** | 引入库依赖即可使用 |

2、快速上手

2.1、定义 DataStore
直接创建一个继承自 `BaseDataStore` 的 `object` (单例) 或 `class`。

```kotlin
import com.d10ng.datastore.delegate.BaseDataStore

// "app_settings" 是存储文件的名称
object AppSettings : BaseDataStore("app_settings") {
    
    // 1. 基础类型：指定默认值即可
    val username by stringPreference("default_user")
    val age by intPreference(18)
    val isVip by booleanPreference(false)
    
    // 2. 枚举类型：自动支持
    val theme by enumPreference(Theme.LIGHT)
    
    // 3. 对象类型：需配合 @Serializable 注解
    val userInfo by objectPreference(User(id = 0, name = "unknown"))
    
    // 4. 动态 Key (类似 Map)：用于存储一组同类型的数据
    // 例如：存储不同用户的分数，Key=userId, Value=Score
    val userScores by mapPreference<String, Int>(default = 0)
}
```

2.2、调用方式

无需记忆 `getXXXFlow`, `setXXXSync` 等生成方法，新版 API 更加统一：

```kotlin
// --- 读取数据 ---
// 1. 响应式流 (Flow)
AppSettings.username.flow.collect { name -> println(name) }

// 2. 挂起函数 (Suspend)
val age = AppSettings.age.get()

// 3. 同步阻塞 (Sync)
val isVip = AppSettings.isVip.getSync()

// --- 写入数据 ---
// 1. 挂起函数
AppSettings.username.set("New Name")

// 2. 同步阻塞
AppSettings.age.setSync(20)

// --- 动态 Key 使用 ---
// 像 Map 一样使用 [] 操作符
AppSettings.userScores["user_001"].set(100)
val score = AppSettings.userScores["user_001"].get()
```

3、从旧版迁移指南

3.1、迁移步骤
1.  **移除 KSP 插件** (可选)：如果项目中不再有其他 KSP 依赖，可以移除 `build.gradle.kts` 中的 KSP 插件配置。
2.  **重写 DataStore 定义**：将带有 `@PreferenceDataStore` 的接口改写为继承 `BaseDataStore` 的 `object`。
3.  **替换调用处**：利用 IDE 的“查找替换”功能，更新调用逻辑。

3.2、代码对比

**旧版 (Interface + Annotation)**
```kotlin
@PreferenceDataStore(name = "settings")
interface SettingData {
    @PreferenceKey(default = "\"d10ng\"")
    val username: String
}

// 调用
SettingDataStore.instant.getUsernameFlow()
SettingDataStore.instant.setUsername("new")
```

**新版 (Delegate)**
```kotlin
object SettingData : BaseDataStore("settings") {
    val username by stringPreference("d10ng")
}

// 调用
SettingData.username.flow
SettingData.username.set("new")
```

3.3、常见问题与注意事项

*   **⚠️ Data Class 序列化**：使用 `objectPreference` 或 `mapPreference` 存储对象时，该对象类**必须**添加 `@Serializable` 注解。
*   **兼容性**：新旧方案底层均使用 Jetpack DataStore，只要**文件名 (name)** 和 **Key 名称** 保持一致，数据是可以**无缝互通**的。
    *   旧版生成的 Key 默认为属性名（例如 `username`）。
    *   新版默认也使用属性名作为 Key。
    *   **结论**：迁移后用户数据**不会丢失**。

---

# 旧版使用文档 (KSP 模式)

> 仅供维护旧代码参考，新项目建议直接使用 0.3.0+ 新版方案。

1、创建一个`datastore`，例如：
```kotlin
// 创建一个名为settings的datastore，name参数可以省略，默认为类名
// 该类会自动生成一个SettingsDataStore类，用于操作datastore
@PreferenceDataStore(name = "settings")
interface SettingData
```

2、创建一个`datastore`的`key`，例如：
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

3、创建带参数的`datastore`的`key`，例如：
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

4 、设置默认值
```kotlin
@PreferenceKey(default = "\"d10ng\"")
val username: String

@PreferenceKey(default = "123456")
val age: Int
```
> 这种方式就是将您所输入的字符串作为模版拼接到生成的代码中，是一个取巧的方式，待后续KSP支持读取参数值后可能会弃用；
