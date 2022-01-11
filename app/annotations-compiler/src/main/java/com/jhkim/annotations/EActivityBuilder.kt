package com.jhkim.annotations

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.jhkim.annotations.util.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

class EActivityBuilder(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) {
    @OptIn(KotlinPoetKspPreview::class)
    fun makeBuilderFile(classDeclaration: KSClassDeclaration)  {
        val args = classDeclaration.getProperties(Extra::class.java)
        val className = classDeclaration.toClassName()
        val buildClassName = ClassName(className.packageName, "${className.simpleName}Builder")

        logger.info("process ${className.simpleName}")
        val file = FileSpec.builder(buildClassName.packageName, buildClassName.simpleName)
            .addType(
                TypeSpec.objectBuilder(buildClassName)
                .addFunction(
                    FunSpec.builder("bundle")
                        .returns(ClassEx.Bundle)
                        .addParameters(args.toParameterSpec())
                        .addStatement("return %L", args.toBundleOf())
                        .build()
                )
                .addFunction(
                    FunSpec.builder("intent")
                        .addParameter("context", ClassEx.Context)
                        .addParameters(args.toParameterSpec())
                        .returns(ClassEx.Intent)
                        .beginControlFlow("return Intent(context, %L::class.java).apply", className.simpleName)
                        .addStatement("putExtras(%L)", args.toBundleOf())
                        .endControlFlow()
                        .build()
                )
                .addFunction(
                    FunSpec.builder("startActivity")
                        .addParameter("context", ClassEx.Context)
                        .addParameters(args.toParameterSpec())
                        .addStatement("context.startActivity(intent(context, %L))", args.toArgsString())
                        .build()
                )
                .addFunction(
                    FunSpec.builder("inject")

                        .addParameter("activity", className)
                        .addStatement("val bundle = activity.intent?.extras ?: return")
                        //.addInjectStatements(args, "activity.")
                        .addStatements(args.toBundleStrings("activity."))
                        .build()

                )
                .build()
            )
            .addImport("androidx.core.os","bundleOf")
            .build()
        file.writeTo(codeGenerator, Dependencies(true, classDeclaration.containingFile!!))
    }
}
