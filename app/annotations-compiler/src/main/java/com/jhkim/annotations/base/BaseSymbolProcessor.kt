package com.jhkim.annotations.base

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid

abstract class BaseSymbolProcessor(environment: SymbolProcessorEnvironment, private val cls:Class<*>) : SymbolProcessor {
    val codeGenerator = environment.codeGenerator
    val logger = environment.logger

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation(cls.canonicalName!!)
        val used = symbols.filter { it is KSClassDeclaration /*&& it.validate()*/ }
        val unused = symbols.filterNot { it is KSClassDeclaration /*&& it.validate()*/ }
//
//        unused.forEach {
//            logger.warn("skip : ${(it as KSClassDeclaration).simpleName.asString()}")
//        }
        used.forEach {
            it.accept(object : KSVisitorVoid() {
                override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
                    accept(classDeclaration)
                }
            }, Unit)
        }
        return unused.toList()
    }

    protected abstract fun accept(classDeclaration: KSClassDeclaration)
}