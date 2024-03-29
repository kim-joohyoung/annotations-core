package com.jhkim.annotations

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.jhkim.annotations.CodeBuild.addInjectFunction
import com.jhkim.annotations.util.ClassNameEx
import com.jhkim.annotations.util.getAllProperties
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

//@OptIn(KotlinPoetKspPreview::class, KspExperimental::class)
@OptIn(KspExperimental::class)
class ActivityBuilderGen(private val classDeclaration: KSClassDeclaration, private val codeGenerator: CodeGenerator, private val logger: KSPLogger) {
    private val checkSuperClass = classDeclaration.getAnnotationsByType(ActivityBuilder::class).first().checkSuperClass
    private val args = classDeclaration.getAllProperties(Extra::class.java, checkSuperClass)
    private val className = classDeclaration.toClassName()
    private val buildClassName = ClassName(className.packageName, "${className.simpleName}Builder")

    fun makeBuilderFile()  {
        logger.info("process ${className.simpleName}")
        val file = FileSpec.builder(buildClassName.packageName, buildClassName.simpleName)
            .addType(
                TypeSpec.objectBuilder(buildClassName)
                .addFunction(
                    FunSpec.builder("intent")
                        .addParameter("context", ClassNameEx.Context)
                        .addParameters(args.toParameterSpecNull())
                        .returns(ClassNameEx.Intent)
                        .beginControlFlow("return Intent(context, %L::class.java).apply", className.simpleName)
                        .addStatement("putExtras(%L)", args.bundleOf())
                        .endControlFlow()
                        .build()
                )
                .addFunction(
                    FunSpec.builder("startActivity")
                        .addParameter("context", ClassNameEx.Context)
                        .addParameters(args.toParameterSpecNull())
                        .addStatement("context.startActivity(intent(%L))", args.toArgsString("context"))
                        .build()
                )
                .addInjectFunction(className, args, InjectType.Activity)
                .build()
            )
            .addImport("androidx.core.os","bundleOf")
            .addImport("com.jhkim.annotations", "fromBundle")
            .addImport("com.jhkim.annotations", "hasBundle")
            .build()
        file.writeTo(codeGenerator, Dependencies(true, classDeclaration.containingFile!!))
    }
}
