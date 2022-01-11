package com.jhkim.annotations

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName
import com.squareup.kotlinpoet.ksp.writeTo
import com.jhkim.annotations.util.*

class EFragmentBuilder(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) {
    @OptIn(KotlinPoetKspPreview::class)
    fun makeBuilderFile(classDeclaration: KSClassDeclaration) {
        val args = classDeclaration.getProperties(Arg::class.java)
        val className = classDeclaration.toClassName()
        val buildClassName = ClassName(className.packageName, "${className.simpleName}Builder")

        logger.info("process ${className.simpleName}")
        val file = FileSpec.builder(buildClassName.packageName, buildClassName.simpleName)
            .addType(
                TypeSpec.classBuilder(buildClassName)
                    .primaryConstructor(args)
                    .addFunction(
                        FunSpec.builder("bundle")
                            .returns(ClassEx.Bundle)
                            .addStatement(
                                "return bundleOf(%L)",
                                args.joinString { name, _ -> "\"$name\" to $name" })
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("build")
                            .returns(className)
                            .beginControlFlow(
                                "return %L().apply",
                                className.simpleName
                            )
                            .addStatement("arguments = bundle()")
                            .endControlFlow()
                            .build()
                    )
                    .build()
            )
            .addFunction(
                FunSpec.builder("injectArgs")
                    .receiver(className)
                    .addStatement("val bundle = arguments ?: return")
                    .addStatements(args.toBundleStrings())
                    .build()
            )
            .addImport("androidx.core.os", "bundleOf")
        if(classDeclaration.hasCompanion()){
            file.addFunction(
                FunSpec.builder("newInstance")
                    .receiver(className.nestedClass("Companion"))
                    .addParameters(args.map {
                        ParameterSpec(
                            it.simpleName.asString(),
                            it.type.toTypeName()
                        )
                    })
                    .addStatement("return %LBuilder(%L).build()", className.simpleName,args.joinString { name, _ -> name } )
                    .build()
            )
        }
//        fun FirstFragment.Companion.newInstance(arg1 : String, arg2 : String?) =
//            FirstFragmentBuilder(arg1, arg2).build()
 //           .build()
        file.build().writeTo(codeGenerator, Dependencies(true, classDeclaration.containingFile!!))
    }
}