package com.jhkim.annotations

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.jhkim.annotations.base.BaseSymbolProcessor

class LauncherProcessor : BaseSymbolProcessor(Launcher::class.java) {
    override fun accept(classDeclaration: KSClassDeclaration) {
        LauncherBuilder(codeGenerator,logger).makeBuilderFile(classDeclaration)
    }
}

class LauncherProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return LauncherProcessor().apply {
            init(environment.codeGenerator, environment.logger)
        }
    }
}