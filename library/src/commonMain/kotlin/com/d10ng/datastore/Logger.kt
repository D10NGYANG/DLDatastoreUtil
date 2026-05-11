package com.d10ng.datastore

import com.d10ng.log.LoggerFactory

/**
 * 日志
 * @Author d10ng
 * @Date 2024/9/23 14:09
 */
internal val log by lazy { LoggerFactory.create("datastore") }

// 提供给外部使用的日志名，避免冲突
val DLDatastoreLog by lazy { log }

internal fun logPreferenceRead(keyName: String, value: Any?, isDefault: Boolean) {
    val source = if (isDefault) "default" else "stored"
    log.i { "读取键值对数据 key=$keyName, value=$value, source=$source" }
}

internal fun logPreferenceWrite(keyName: String, value: Any?) {
    log.i { "写入键值对数据 key=$keyName, value=$value" }
}