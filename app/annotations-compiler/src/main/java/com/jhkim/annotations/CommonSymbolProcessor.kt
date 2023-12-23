package com.jhkim.annotations

import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.jhkim.annotations.util.ClassNameEx
import com.jhkim.annotations.util.isTypeOf

class CommonSymbolProcessor(environment: SymbolProcessorEnvironment) :
    SymbolProcessor {
    private val codeGenerator = environment.codeGenerator
    private val logger = environment.logger

    override fun process(resolver: Resolver): List<KSAnnotated> {
        processInternal(resolver, ActivityBuilder::class.java){
            if(it.isTypeOf(ClassNameEx.Activity))
                ActivityBuilderGen(it, codeGenerator, logger).makeBuilderFile()
            else
                logger.error("@ActivityBuilder error in ${it.simpleName.asString()}", it)
        }
        processInternal(resolver, FragmentBuilder::class.java){
            if(it.isTypeOf(ClassNameEx.Fragment))
                FragmentBuilderGen(it, codeGenerator, logger).makeBuilderFile()
            else
                logger.error("@FragmentBuilder error in ${it.simpleName.asString()}", it)
        }
        processInternal(resolver, ActivityLauncher::class.java){
            if(it.isTypeOf(ClassNameEx.ComponentActivity))
                ActivityLauncherGen(it, codeGenerator, logger).makeBuilderFile()
            else
                logger.warn("@ActivityLauncher error in ${it.simpleName.asString()}", it)
        }
        return emptyList()
    }

    private fun processInternal(resolver: Resolver, cls:Class<*>, callback : (classDeclaration: KSClassDeclaration) -> Unit): List<KSAnnotated> {
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
                    callback(classDeclaration)
                }
            }, Unit)
        }
        return unused.toList()
    }
}


class CommonSymbolProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return CommonSymbolProcessor(environment)
    }
}