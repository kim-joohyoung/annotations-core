package com.jhkim.annotations

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated

class EFragmentProcessor : BaseSymbolProcessor(EFragment::class.java) {
    override fun accept(resolver: Resolver, it: KSAnnotated) {
        it.accept(EFragmentVisitor(codeGenerator, logger), Unit)
    }
//    private lateinit var codeGenerator: CodeGenerator
//    private lateinit var logger: KSPLogger
//
//    fun init(codeGenerator: CodeGenerator, logger: KSPLogger) {
//        this.codeGenerator = codeGenerator
//        this.logger = logger
//    }
//
//    override fun process(resolver: Resolver): List<KSAnnotated> {
//        val symbols = resolver.getSymbolsWithAnnotation(EFragment::class.java.canonicalName!!)
//        val ret = symbols.filterNot { it.validate() }
//
//        symbols
//            .filter { it is KSClassDeclaration && it.validate()&& isTypeOf(it, "androidx.fragment.app.Fragment") }
//            .forEach {
//                it.accept(FragmentVisitor(resolver, codeGenerator, logger), Unit)
//            }
//        return ret.toList()
//    }

}

class EFragmentProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return EFragmentProcessor().apply {
            init(environment.codeGenerator, environment.logger)
        }
    }
}