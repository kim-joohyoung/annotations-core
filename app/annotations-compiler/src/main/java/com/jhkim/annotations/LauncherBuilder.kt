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

class LauncherBuilder(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) {
    @OptIn(KotlinPoetKspPreview::class)
    fun makeBuilderFile(classDeclaration: KSClassDeclaration) {
        val args = classDeclaration.getProperties(Extra::class.java)
        val results = classDeclaration.getProperties(ResultExtra::class.java)
        val className = classDeclaration.toClassName()
        val buildClassName = ClassName(className.packageName, "${className.simpleName}Launcher")

        logger.info("process ${className.simpleName}")
        val file = FileSpec.builder(buildClassName.packageName, buildClassName.simpleName)
            .addType(
                TypeSpec.classBuilder(buildClassName)
                    .addProperty(
                        PropertySpec.builder("launcher", ClassEx.ActivityResult)
                            .addModifiers(KModifier.LATEINIT, KModifier.PRIVATE)
                            .mutable(true)
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder("callback",
                            LambdaTypeName.get(
                                returnType = Unit::class.java.asTypeName(),
                                parameters = results.toParameterSpec()
                            ).copy(true), KModifier.PRIVATE)
                            .mutable(true)
                            .initializer("null")
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("register")
                            .addParameter("activity", ClassEx.ComponentActivity)
                            .beginControlFlow("launcher = activity.registerForActivityResult(%LContract()){bundle->", className.simpleName)
                            .beginControlFlow("bundle?.let {")
                            .addStatements(results.toBundleStrings("val ", "it"))
                            .addStatement("callback?.invoke(%L)", results.toArgsString())
                            .endControlFlow()
                            .endControlFlow()
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("launcher")
                            .addParameters(args.toParameterSpec())
                            .addParameter("callback", LambdaTypeName.get(
                                returnType = Unit::class.java.asTypeName(),
                                parameters = results.toParameterSpec()
                            ))
                            .addStatement("this.callback = callback")
                            .addStatement("launcher.launch(%L)", args.toBundleOf())
                            .build()
                    )
                    .addType(
                        TypeSpec.classBuilder("${className.simpleName}Contract")
                            .superclass(ClassEx.ActivityResultContract)
                            .addFunction(
                                FunSpec.builder("createIntent")
                                    .addParameter("context", ClassEx.Context)
                                    .addParameter("input", ClassEx.Bundle)
                                    .addModifiers(KModifier.OVERRIDE)
                                    .returns(ClassEx.Intent)
                                    .addStatement("return Intent(context, %L::class.java).also { it.putExtras(input) }", className.simpleName)
                                    .build()
                            )
                            .addFunction(
                                FunSpec.builder("parseResult")
                                    .addParameter("resultCode", Int::class.java)
                                    .addParameter("intent", ClassEx.IntentNullable)
                                    .addModifiers(KModifier.OVERRIDE)
                                    .returns(ClassEx.BundleNullable)
                                    .addStatement("return intent?.extras")
                                    .build()
                            )
                            .build()
                    )
                    .addType(TypeSpec.companionObjectBuilder()
                        .addFunction(
                            FunSpec.builder("setResult")
                                .addParameter("activity", className)
                                .addParameter("resultCode", Int::class.java)
                                .addStatement("""
                        activity.apply{
                            setResult(resultCode, Intent().apply {
                                putExtras(%L)
                            })
                        }                  
                    """.trimIndent(), results.toBundleOf())
                                .build()
                        )
                        .build()
                    )
                    .build()
            )

            .addImport("androidx.core.os","bundleOf")
            .build()
        file.writeTo(codeGenerator, Dependencies(true, classDeclaration.containingFile!!))
    }
}
