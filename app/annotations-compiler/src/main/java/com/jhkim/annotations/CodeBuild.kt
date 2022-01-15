package com.jhkim.annotations

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.jhkim.annotations.util.*
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview

enum class InjectType {
    Fragment,
    Activity,
    Variable
}

object  CodeBuild{

    fun injectBuilder(className : ClassName, args: List<ResultData>, injectType : InjectType): FunSpec {
        val builder = FunSpec.builder("inject")
            .returns(Boolean::class)
        when(injectType){
            InjectType.Fragment -> builder.addParameter("fragment", className)
            InjectType.Activity -> builder.addParameter("activity", className)
            else -> {}
        }
//        builder.addStatements(args.checkBundle(injectType))
        builder.beginControlFlow("try")
        builder.addStatements(args.extractBundle(injectType))
        builder.addStatement("return true")
        builder.nextControlFlow("catch(ex:Exception)")
        builder.endControlFlow()
        builder.addStatement("return false")
        return builder.build()
    }
    private fun List<ResultData>.checkBundle(bundleType : InjectType) = map {
        val name = it.name
        when(bundleType){
            InjectType.Fragment -> "if(fragment.containsKey(\"$name\")) return false"
            InjectType.Activity -> "if(activity.containsKey(\"$name\")) return false"
            InjectType.Variable -> "if(bundle.containsKey(\"$name\")) return false"
        }
    }.toList()

    fun List<ResultData>.extractBundle(bundleType : InjectType) = map {
        val name = it.name
        val type = it.type
        when(bundleType){
            InjectType.Fragment -> "fragment.$name = fragment.extra<$type>(\"$name\")"
            InjectType.Activity -> "activity.$name = activity.extra<$type>(\"$name\")"
            InjectType.Variable -> "val $name = bundle.extra<$type>(\"$name\")"
        }
    }.toList()

//    fun List<ResultData>.extractBundle(bundleType : InjectType) = map {
//        val name = it.name
//        val type = it.type
//        when(bundleType){
//            InjectType.Fragment -> "fragment.$name = fragment.arguments?.get(\"$name\") as $type"
//            InjectType.Activity -> "activity.$name = activity.intent?.extras?.get(\"$name\") as $type"
//            InjectType.Variable -> "var $name = bundle.get(\"$name\") as $type"
//        }
//    }.toList()

    @KotlinPoetKspPreview
    fun bundle(args: List<ResultData>, isParameter :Boolean = true): FunSpec {
        val builder = FunSpec.builder("bundle")
            .returns(ClassNameEx.Bundle)
            .addStatement("return %L", args.bundleOf())
        if(isParameter)
            builder.addParameters(args.toParameterSpec())
        builder.build()
        return builder.build()
    }
}
