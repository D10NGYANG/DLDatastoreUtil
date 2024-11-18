package com.d10ng.datastore.annotation

@Target(AnnotationTarget.CLASS)
annotation class PreferenceDataStore(
    /**
     * 数据存储名称，如果不设置则默认使用类名，全局不能重复，只能使用英文数字下划线
     */
    val name: String = ""
)