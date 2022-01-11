package com.jhkim.annotations

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.jhkim.annotations.base.BaseSymbolProcessor

class EFragmentProcessor(environment: SymbolProcessorEnvironment) : BaseSymbolProcessor(environment, EFragment::class.java) {
    override fun accept(classDeclaration: KSClassDeclaration) {
        EFragmentBuilder(codeGenerator, logger).makeBuilderFile(classDeclaration)
    }
}

class EFragmentProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return EFragmentProcessor(environment)
    }
}