package com.jhkim.annotations

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

abstract class BaseSymbolProcessor(private val cls:Class<*>) : SymbolProcessor {
    protected lateinit var codeGenerator: CodeGenerator
    protected lateinit var logger: KSPLogger
    private val processed = mutableListOf<KSAnnotated>()

    fun init(codeGenerator: CodeGenerator, logger: KSPLogger) {
        this.codeGenerator = codeGenerator
        this.logger = logger
    }

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(cls.canonicalName!!)
        val used = symbols.filter { it is KSClassDeclaration /*&& it.validate()*/ }
        val unused = symbols.filterNot { it is KSClassDeclaration /*&& it.validate()*/ }
//
//        unused.forEach {
//            logger.warn("skip : ${(it as KSClassDeclaration).simpleName.asString()}")
//        }
        used.forEach {
//            logger.warn("process : ${(it as KSClassDeclaration).simpleName.asString()}")
            accept(resolver, it)
        }
        return unused.toList()
    }

    protected abstract fun accept(resolver: Resolver, it: KSAnnotated)
}