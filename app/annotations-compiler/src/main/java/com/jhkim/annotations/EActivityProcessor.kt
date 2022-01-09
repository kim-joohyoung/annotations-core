package com.jhkim.annotations

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated

class EActivityProcessor : BaseSymbolProcessor(EActivity::class.java) {
    override fun accept(resolver: Resolver, it: KSAnnotated) {
        it.accept(EActivityVisitor(codeGenerator, logger), Unit)
    }
}

class EActivityProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return EActivityProcessor().apply {
            init(environment.codeGenerator, environment.logger)
        }
    }
}