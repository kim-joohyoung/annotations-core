package com.jhkim.annotations

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.jhkim.annotations.util.*
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview

enum class InjectType {
    Fragment,
    Activity,
    Variable
}

object  CodeBuild{

    fun TypeSpec.Builder.addInjectFunction(className : ClassName, args: List<ResultData>, injectType : InjectType): TypeSpec.Builder {
        if(args.isNotEmpty())
            this.addFunction(injectBuilder(className, args, injectType))
        return this
    }

    private fun injectBuilder(className : ClassName, args: List<ResultData>, injectType : InjectType): FunSpec {
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
            InjectType.Fragment -> "fragment.$name = fragment.fromBundle<$type>(\"$name\")"
            InjectType.Activity -> "activity.$name = activity.fromBundle<$type>(\"$name\")"
            InjectType.Variable -> "val $name = bundle.fromBundle<$type>(\"$name\")"
        }
    }.toList()
}
