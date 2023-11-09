package com.d10ng.datastore.app.data

import com.d10ng.datastore.app.constant.SexType
import kotlinx.serialization.Serializable

/**
 *
 * @Author d10ng
 * @Date 2023/11/9 17:53
 */
@Serializable
data class Person(
    // 姓名
    val name: String,
    // 年龄
    val age: Int,
    // 身高
    val height: Float,
    // 性别
    val sex: SexType,
)
