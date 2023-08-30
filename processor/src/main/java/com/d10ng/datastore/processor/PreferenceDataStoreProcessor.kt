package com.d10ng.datastore.processor

import com.d10ng.datastore.annotation.PreferenceDataStore
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.validate

class PreferenceDataStoreProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger,
    private val options: Map<String, String>
): SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {

        // Get all classes with @PreferenceDataStore annotation
        val symbols = resolver
            .getSymbolsWithAnnotation(PreferenceDataStore::class.java.name)
            .filterIsInstance<KSClassDeclaration>()

        // If there is no class with @PreferenceDataStore annotation, return empty list
        if (symbols.iterator().hasNext().not()) return emptyList()

        // Iterate over all classes with @PreferenceDataStore annotation
        symbols.forEach { symbol ->
            // If the class is not a interface, print error message and return empty list
            if (symbol.classKind.type != "interface") {
                logger.error("@PreferenceDataStore can only be used on interface, but ${symbol.qualifiedName?.asString()} is not a interface")
                return emptyList()
            }

            symbol.accept(PreferenceDataStoreVisitor(codeGenerator, logger, options), Unit)
        }

        return symbols.filterNot { it.validate() }.toList()
    }
}