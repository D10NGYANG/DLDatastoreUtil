package com.d10ng.datastore.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

class PreferenceDataStoreProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
): SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        logger.info("DLDatastoreUtil-Processor 开始处理")

        // 查找所有使用@PreferenceDataStore注解的类
        val symbols = resolver
            .getSymbolsWithAnnotation("com.d10ng.datastore.annotation.PreferenceDataStore")
            .filterIsInstance<KSClassDeclaration>()

        // Iterate over all classes with @PreferenceDataStore annotation
        symbols.forEach { symbol ->
            // If the class is not a interface, print error message and return empty list
            if (symbol.classKind.type != "interface") {
                logger.error("@PreferenceDataStore can only be used on interface, but ${symbol.qualifiedName?.asString()} is not a interface")
            } else {
                logger.info("symbol: ${symbol.qualifiedName?.asString()}")
                symbol.accept(PreferenceDataStoreVisitor(codeGenerator, logger, options), Unit)
            }
        }
        return emptyList()
    }
}