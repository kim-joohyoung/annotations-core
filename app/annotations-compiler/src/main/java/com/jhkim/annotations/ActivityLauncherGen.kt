package com.jhkim.annotations

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.jhkim.annotations.CodeBuild.extractBundle
import com.jhkim.annotations.util.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

@Suppress("UNCHECKED_CAST")
class ActivityLauncherGen(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) {

    @OptIn(KotlinPoetKspPreview::class, KspExperimental::class)
    fun makeBuilderFile(classDeclaration: KSClassDeclaration) {
        val args = classDeclaration.getProperties(Extra::class.java)
        val className = classDeclaration.toClassName()
        val buildClassName = ClassName(className.packageName, "${className.simpleName}Launcher")
        val returns = classDeclaration.getAnnotations().toList()

        logger.info("process ${className.simpleName}")
        val file = FileSpec.builder(buildClassName.packageName, buildClassName.simpleName)
            .addType(
                TypeSpec.classBuilder(buildClassName)
                    .addProperty(
                        PropertySpec.builder("launcher", ClassNameEx.ActivityResult)
                            .addModifiers(KModifier.LATEINIT, KModifier.PRIVATE)
                            .mutable(true)
                            .build()
                    )
                    .addProperty(
                        PropertySpec.builder("callback",
                            LambdaTypeName.get(
                                returnType = Unit::class.java.asTypeName(),
                                parameters = returns.toParameterSpec()
                            ).copy(true), KModifier.PRIVATE)
                            .mutable(true)
                            .initializer("null")
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("parseBundle")
                            .addModifiers(KModifier.PRIVATE)
                            .addParameter("bundle", ClassNameEx.BundleNullable)
                            .beginControlFlow("bundle?.let {")
                            //.addStatements(returns.getBundleList("it"))
                            .addStatements(returns.extractBundle(InjectType.Variable))
                            .addStatement("callback?.invoke(%L)", returns.toArgsString())
                            .endControlFlow()
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("register")
                            .addParameter("activity", ClassNameEx.ComponentActivity)
                            .beginControlFlow("launcher = activity.registerForActivityResult(%LContract())", className.simpleName)
                            .addStatement("parseBundle(it)")
                            .endControlFlow()
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("register")
                            .addParameter("fragment", ClassNameEx.Fragment)
                            .beginControlFlow("launcher = fragment.registerForActivityResult(%LContract())", className.simpleName)
                            .addStatement("parseBundle(it)")
                            .endControlFlow()
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("launch")
                            .addParameters(args.toParameterSpec())
                            .addParameter("callback", LambdaTypeName.get(
                                returnType = Unit::class.java.asTypeName(),
                                parameters = returns.toParameterSpec()
                            ))
                            .addStatement("this.callback = callback")
                            .addStatement("launcher.launch(%L)", args.bundleOf())
                            .build()
                    )
                    .addType(
                        TypeSpec.classBuilder("${className.simpleName}Contract")
                            .superclass(ClassNameEx.ActivityResultContract)
                            .addFunction(
                                FunSpec.builder("createIntent")
                                    .addParameter("context", ClassNameEx.Context)
                                    .addParameter("input", ClassNameEx.Bundle)
                                    .addModifiers(KModifier.OVERRIDE)
                                    .returns(ClassNameEx.Intent)
                                    .addStatement("return Intent(context, %L::class.java).also { it.putExtras(input) }", className.simpleName)
                                    .build()
                            )
                            .addFunction(
                                FunSpec.builder("parseResult")
                                    .addParameter("resultCode", Int::class.java)
                                    .addParameter("intent", ClassNameEx.IntentNullable)
                                    .addModifiers(KModifier.OVERRIDE)
                                    .returns(ClassNameEx.BundleNullable)
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
                                .addParameters(returns.toParameterSpec())
                                .addStatement("activity.setResult(resultCode, Intent().apply {")
//                                .addStatements(returns.mapIndexed { index, _ -> "\tputExtra(\"param$index\", param$index)"})
                                .addStatement("\tputExtras(%L)", returns.bundleOf())
                                .addCode("})")
                                .build()
                        )
                        .addFunction(
                            CodeBuild.injectBuilder(className, args, InjectType.Activity)

                        )
                        .build()
                    )
                    .build()
            )

            .addImport("androidx.core.os","bundleOf")
            .addImport("com.jhkim.annotations", "extra")
            .build()
        file.writeTo(codeGenerator, Dependencies(true, classDeclaration.containingFile!!))
    }
}
