package com.d10ng.datastore.processor

import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider

class PreferenceDataStoreSymbolProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return PreferenceDataStoreProcessor(
            environment.codeGenerator,
            environment.logger,
            environment.options
        )
    }
}