package com.d10ng.datastore.annotation

import kotlin.reflect.KClass

@Target(AnnotationTarget.PROPERTY)
annotation class PreferenceKey (
    /**
     * 组成键的参数，每个键值作为一个参数类型，最终与变量名组合成键
     */
    val keys: Array<KClass<*>> = []
)
