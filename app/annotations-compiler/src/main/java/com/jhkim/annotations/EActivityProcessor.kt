package com.jhkim.annotations

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.jhkim.annotations.base.BaseSymbolProcessor

class EActivityProcessor(environment: SymbolProcessorEnvironment) : BaseSymbolProcessor(environment, EActivity::class.java) {
    override fun accept(classDeclaration: KSClassDeclaration) {
        EActivityBuilder(codeGenerator,logger).makeBuilderFile(classDeclaration)
    }
}

class EActivityProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return EActivityProcessor(environment)
    }
}