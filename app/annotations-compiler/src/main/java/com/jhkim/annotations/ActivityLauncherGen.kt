package com.jhkim.annotations

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.jhkim.annotations.CodeBuild.addInjectFunction
import com.jhkim.annotations.CodeBuild.extractBundle
import com.jhkim.annotations.util.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

@OptIn(KspExperimental::class)
class ActivityLauncherGen(private val classDeclaration: KSClassDeclaration, private val codeGenerator: CodeGenerator, private val logger: KSPLogger) {
    private val checkSuperClass = classDeclaration.getAnnotationsByType(ActivityLauncher::class).first().checkSuperClass
    private val args = classDeclaration.getAllProperties(Extra::class.java, checkSuperClass)
    private val className = classDeclaration.toClassName()
    private val buildClassName = ClassName(className.packageName, "${className.simpleName}Launcher")
    private val returns = classDeclaration.getAnnotations(Result::class).toList()

    fun makeBuilderFile() {
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
                            .extractBundle(InjectType.Variable, returns)
                            .addStatement("callback?.invoke(%L)", returns.toArgsString())
                            .endControlFlow()
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("register")
                            .addParameter("activity", ClassNameEx.ComponentActivity)
                            .beginControlFlow("launcher = activity.registerForActivityResult(ActivityContract(%L::class.java))", className.simpleName)
                            .addStatement("parseBundle(it)")
                            .endControlFlow()
                            .build()
                    )
                    .addFunction(
                        FunSpec.builder("register")
                            .addParameter("fragment", ClassNameEx.Fragment)
                            .beginControlFlow("launcher = fragment.registerForActivityResult(ActivityContract(%L::class.java))", className.simpleName)
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
                        .addInjectFunction(className, args, InjectType.Activity)
                        .build()
                    )
                    .build()
            )
            .addImport("android.content","Intent")
            .addImport("androidx.core.os","bundleOf")
            .addImport("com.jhkim.annotations", "fromBundle")
            .addImport("com.jhkim.annotations", "ActivityContract")
            .build()
        file.writeTo(codeGenerator, Dependencies(true, classDeclaration.containingFile!!))
    }
}
