package com.jhkim.annotations

import com.google.devtools.ksp.KspExperimental
import com.google.devtools.ksp.getAnnotationsByType
import com.google.devtools.ksp.isAbstract
import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.jhkim.annotations.CodeBuild.addInjectFunction
import com.jhkim.annotations.CodeBuild.extractBundle
import com.jhkim.annotations.util.*
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.writeTo

@OptIn(KotlinPoetKspPreview::class, KspExperimental::class)
class FragmentBuilderGen(private val classDeclaration: KSClassDeclaration, private val codeGenerator: CodeGenerator, private val logger: KSPLogger) {
    private val annotation = classDeclaration.getAnnotationsByType(FragmentBuilder::class).first()
    private val checkSuperClass = annotation.checkSuperClass
    private val isListener = annotation.listener

    private val args = classDeclaration.getAllProperties(Arg::class.java, checkSuperClass)
    private val className = classDeclaration.toClassName()
    private val buildClassName = ClassName(className.packageName, "${className.simpleName}Builder")
    private val returns = classDeclaration.getAnnotations(Result::class)


    fun makeBuilderFile() {
        logger.info("process ${className.simpleName}")
        val file = FileSpec.builder(buildClassName.packageName, buildClassName.simpleName)
            .addType(
                TypeSpec.objectBuilder(buildClassName)
//                    .primaryConstructor(args)
//                    .addFunction(CodeBuild.bundle(args, false))
                    .addBuildFunc()
                    .addInjectFunction(className, args, InjectType.Fragment)
                    .addListenerObjectBuilder()
                    .build()
            )
            .addImport("androidx.core.os", "bundleOf")
            .addImport("com.jhkim.annotations", "fromBundle")

        file.build().writeTo(codeGenerator, Dependencies(true, classDeclaration.containingFile!!))
    }

    private fun TypeSpec.Builder.addBuildFunc(): TypeSpec.Builder {
        if (classDeclaration.isAbstract())
            return this
        addFunction(
            FunSpec.builder("build")
                .returns(className)
                .addParameters(args.toParameterSpec())
                .addStatement("val frag= %L()", className.simpleName)
                .addStatement("frag.arguments = %L", args.bundleOf())
                .addStatement("return frag")
                .build()
        )
        return this
    }

    private fun TypeSpec.Builder.addListenerObjectBuilder(): TypeSpec.Builder {
        if (isListener) {
            addProperty(
                PropertySpec.builder("requestKey", String::class, KModifier.PRIVATE)
                    .initializer("\"$className\"")
                    .build()
            )
            .addFunction(
                FunSpec.builder("register")
                    .addParameter("fragment", ClassNameEx.Fragment)
                    .addParameter(
                        "callback", LambdaTypeName.get(
                            returnType = Unit::class.java.asTypeName(),
                            parameters = returns.toParameterSpec()
                        )
                    )
                    .addStatement("register(fragment.parentFragmentManager, fragment, callback)")
                    .build()
            )
            .addFunction(
                FunSpec.builder("register")
                    .addModifiers(KModifier.PRIVATE)
                    .addParameter("fm", ClassNameEx.FragmentManager)
                    .addParameter("lifecycleOwner", ClassNameEx.LifecycleOwner)
                    .addParameter(
                        "callback", LambdaTypeName.get(
                            returnType = Unit::class.java.asTypeName(),
                            parameters = returns.toParameterSpec()
                        )
                    )
                    .beginControlFlow("fm.setFragmentResultListener(requestKey, lifecycleOwner)")
                    .addStatement(" _, bundle ->")
                    .beginControlFlow("try")
                    .addStatements(returns.extractBundle(InjectType.Variable))
                    .addStatement("callback(%L)", returns.toArgsString())
                    .nextControlFlow("catch(ex:Exception)")
                    .endControlFlow()
                    .endControlFlow()
                    .build()
            )
            .addFunction(
                FunSpec.builder("setResult")
                    //.addParameter("fragment", className)
                    .addParameter("fm", ClassNameEx.FragmentManager)
                    .addParameters(returns.toParameterSpec())
                    .addStatement("fm.setFragmentResult(requestKey, %L)", returns.bundleOf())
                    .build()
            )
        }
        return this
    }
}