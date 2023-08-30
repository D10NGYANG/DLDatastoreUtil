package com.d10ng.datastore.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import java.io.OutputStream
import java.util.Locale

class PreferenceDataStoreVisitor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
): KSVisitorVoid() {

    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        // Get package name and class name
        val packageName = classDeclaration.packageName.asString()
        val className = classDeclaration.simpleName.asString()
        val fileName = "${className}Store"

        // 获取类注解中的参数
        val annotation = classDeclaration.annotations.find { it.shortName.asString() == "PreferenceDataStore" }
        val name = annotation?.arguments?.find { it.name?.asString() == "name" }?.value?.toString()?.replace("\"", "") ?: className

        logger.info("packageName: $packageName")
        logger.info("className: $className")
        logger.info("fileName: $fileName")
        logger.info("datastore name: $name")

        // 获取所有带有 @PreferenceKey 注解的属性
        val properties = classDeclaration.getAllProperties()
            .filter { it.annotations.any { ann -> ann.shortName.asString() == "PreferenceKey" } }

        // Create file
        val file = codeGenerator.createNewFile(
            dependencies = Dependencies(false),
            packageName = packageName,
            fileName = fileName
        )

        file += "package $packageName\n\n"

        file += "import androidx.datastore.preferences.core.*\n"
        file += "import com.d10ng.datastore.DataStoreOwner\n"
        file += "import kotlinx.coroutines.flow.*\n"
        file += "import kotlinx.coroutines.*\n"
        file += "\n"

        file += "object $fileName : DataStoreOwner(\"${name}\") {\n\n"

        // 遍历所有属性
        properties.forEach { property ->
            // 获取属性名
            val propertyName = property.simpleName.asString()
            // 获取属性类型
            val propertyType = property.type.resolve()
            // 判断属性类型是否为枚举类型
            val isEnum = propertyType.declaration.modifiers.any { it.name.contentEquals("enum", true) }
            val propertyTypePackageName = propertyType.declaration.packageName.asString()
            // 非基础类型的数据，只支持枚举类型
            if (propertyTypePackageName.startsWith("kotlin", false).not() && isEnum.not()) {
                logger.error("Only support basic types and enum types, but ${propertyName}: $propertyTypePackageName")
                return
            }
            val propertyTypeName = if (propertyTypePackageName.startsWith("kotlin", true)) propertyType.toString()
            else propertyType.declaration.qualifiedName?.asString() ?: "String"
            logger.info("${propertyName}: $propertyTypeName")
            // Set只支持String类型
            if (propertyTypeName.startsWith("Set") && !propertyTypeName.endsWith("<String>")) {
                logger.error("Set only supports String type, but ${propertyName}: $propertyTypeName")
                return
            }

            // 获取属性注解中的参数
            val propertyAnnotation = property.annotations.find { it.shortName.asString() == "PreferenceKey" }
            val keys = propertyAnnotation?.arguments?.find { it.name?.asString() == "keys" }?.value as ArrayList<*>?

            // 获取属性的键
            val key = if (keys.isNullOrEmpty()) propertyName else {
                propertyName + ":" + List(keys.size) { index -> "\${key${index}}" }.joinToString(":")
            }

            // 获取输入参数的字符串
            val getInputParams = if (keys.isNullOrEmpty()) "" else {
                List(keys.size) { index -> "key${index}: ${keys[index]}" }.joinToString(", ")
            }
            val setInputParams = if (getInputParams.isEmpty()) "value: $propertyTypeName" else {
                "${getInputParams}, value: $propertyTypeName"
            }

            // 获取调用函数时的输入参数的字符串
            val getCallParams = if (keys.isNullOrEmpty()) "" else {
                List(keys.size) { index -> "key${index}" }.joinToString(", ")
            }
            val setCallParams = if (getCallParams.isEmpty()) "value" else {
                "${getCallParams}, value"
            }

            // 获取函数名称中间的字符串
            val funName = propertyName.formatWithFirstCharUpper()
            // 存储类型
            val propertyTypeKey = getPreferencesKeyStr(propertyTypeName.replace("kotlin.", "", true))

            // 枚举类型需要增加的代码
            val getEnumPlusStr = if (isEnum) "?.let { d -> ${propertyTypeName}.valueOf(d) }" else ""
            val setEnumPlusStr = if (isEnum) ".toString()" else ""

            // 生成属性的 getter 方法
            file += "\tfun get${funName}Flow(${getInputParams}) = dataStore.data.map { it[${propertyTypeKey}(\"${key}\")]${getEnumPlusStr} }\n"
            file += "\tsuspend fun get${funName}(${getInputParams}) = get${funName}Flow(${getCallParams}).first()\n"
            file += "\tfun get${funName}Sync(${getInputParams}) = runBlocking { get${funName}(${getCallParams}) }\n"

            // 生成属性的 setter 方法
            file += "\tsuspend fun set${funName}(${setInputParams}) = dataStore.edit { it[${propertyTypeKey}(\"${key}\")] = value${setEnumPlusStr} }\n"
            file += "\tfun set${funName}Sync(${setInputParams}) = runBlocking { set${funName}(${setCallParams}) }\n"
            file += "\n"
        }

        file += "\n}"

        file.close()
    }

    /**
     * 格式化字符串，首字母大写
     * @receiver String
     * @return String
     */
    private fun String.formatWithFirstCharUpper(): String {
        return this.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(
                Locale.ROOT
            ) else it.toString()
        }
    }

    /**
     * 根据属性类型获取对应的 PreferencesKey 方法名
     * @param propertyType String
     * @return String
     */
    private fun getPreferencesKeyStr(propertyType: String): String {
        return when (propertyType) {
            "String" -> "stringPreferencesKey"
            "Int" -> "intPreferencesKey"
            "Long" -> "longPreferencesKey"
            "Float" -> "floatPreferencesKey"
            "Double" -> "doublePreferencesKey"
            "Boolean" -> "booleanPreferencesKey"
            "Set<String>" -> "stringSetPreferencesKey"
            else -> "stringPreferencesKey"
        }
    }
}

operator fun OutputStream.plusAssign(str: String) = write(str.toByteArray())