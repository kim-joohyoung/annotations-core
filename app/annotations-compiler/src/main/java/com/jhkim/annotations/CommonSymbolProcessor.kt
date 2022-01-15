package com.jhkim.annotations

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import com.jhkim.annotations.util.ClassNameEx
import com.jhkim.annotations.util.isTypeOf
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec

import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.kspDependencies
import com.squareup.kotlinpoet.ksp.writeTo
import java.lang.Exception
import java.sql.Statement

class CommonSymbolProcessor(environment: SymbolProcessorEnvironment) :
    SymbolProcessor {
    private val codeGenerator = environment.codeGenerator
    private val logger = environment.logger

    override fun process(resolver: Resolver): List<KSAnnotated> {
        buildUtilFile(resolver)

        processInternal(resolver, ActivityBuilder::class.java){
            if(it.isTypeOf(ClassNameEx.Activity))
                ActivityBuilderGen(codeGenerator, logger).makeBuilderFile(it)
            else
                logger.error("@ActivityBuilder error in ${it.simpleName.asString()}", it)
        }
        processInternal(resolver, FragmentBuilder::class.java){
            if(it.isTypeOf(ClassNameEx.Fragment))
                FragmentBuilderGen(codeGenerator, logger).makeBuilderFile(it)
            else
                logger.error("@FragmentBuilder error in ${it.simpleName.asString()}", it)
        }
        processInternal(resolver, ActivityLauncher::class.java){
            if(it.isTypeOf(ClassNameEx.ComponentActivity))
                ActivityLauncherGen(codeGenerator, logger).makeBuilderFile(it)
            else
                logger.warn("@ActivityLauncher error in ${it.simpleName.asString()}", it)
        }
        return emptyList()
    }

    @OptIn(KotlinPoetKspPreview::class)
    private fun buildUtilFile(resolver: Resolver) {
        try {
            resolver.getAllFiles().firstOrNull()?.let {
                val data = """
                    package ${it.packageName.asString()}
                    
                    import android.os.Bundle
                    import androidx.fragment.app.Fragment
                    import android.app.Activity
                    
                    fun Activity.containsKey(key: String) = intent.extras?.containsKey(key) ?: false 
                    fun Fragment.containsKey(key: String) = arguments?.containsKey(key) ?: false
                    
                    inline fun <reified T> Activity.extra(key: String) = intent.extras?.get(key) as T 
                    inline fun <reified T> Fragment.extra(key: String) = arguments?.get(key) as T
                    inline fun <reified T> Bundle.extra(key: String) = get(key) as T
                    
                    inline fun <reified T> Activity.extraNotNull(key: String) = requireNotNull(intent.extras?.get(key) as T) { key } 
                    inline fun <reified T> Fragment.extraNotNull(key: String) = requireNotNull(arguments?.get(key) as T) { key } 
                    inline fun <reified T> Bundle.extraNotNull(key: String) = requireNotNull(get(key) as T){key}
                    
                """.trimIndent()
                val out = codeGenerator.createNewFile(
                    Dependencies(true, *resolver.getAllFiles().toList().toTypedArray()),
                    it.packageName.asString(),
                    "BundleUtil"
                )
                out.write(data.toByteArray())
                out.close()
            }
        }
        catch (ex:Exception){
//            logger.error(ex.toString())
        }
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