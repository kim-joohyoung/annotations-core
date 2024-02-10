package com.jhkim.annotations

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeSpec
import com.squareup.kotlinpoet.ksp.toTypeName

enum class InjectType {
    Fragment,
    Activity,
    Variable
}

object  CodeBuild{

    fun TypeSpec.Builder.addInjectFunction(className : ClassName, args: List<ResultData>, injectType : InjectType): TypeSpec.Builder {
        if(args.isNotEmpty())
            this.addFunction(injectBuilder(className, args.filter { it.isMutable }, injectType))
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
        builder.extractBundle(injectType, args)
        builder.addStatement("return true")
        builder.nextControlFlow("catch(ex:Exception)")
        builder.endControlFlow()
        builder.addStatement("return false")
        return builder.build()
    }
//    private fun List<ResultData>.checkBundle(bundleType : InjectType) = map {
//        val name = it.name
//        when(bundleType){
//            InjectType.Fragment -> "if(fragment.containsKey(\"$name\")) return false"
//            InjectType.Activity -> "if(activity.containsKey(\"$name\")) return false"
//            InjectType.Variable -> "if(bundle.containsKey(\"$name\")) return false"
//        }
//    }.toList()

    fun FunSpec.Builder.extractBundle(bundleType : InjectType, items : List<ResultData>): FunSpec.Builder = apply{
        items.forEach {
            val name = it.name
            when(bundleType){
                InjectType.Fragment -> addStatement("fragment.$name = fragment.fromBundle<%T>(\"$name\")", it.type.toTypeName())
                InjectType.Activity -> addStatement("activity.$name = activity.fromBundle<%T>(\"$name\")", it.type.toTypeName())
                InjectType.Variable -> addStatement("val $name = bundle.fromBundle<%T>(\"$name\")", it.type.toTypeName())
            }
        }
    }
//
//    fun List<ResultData>.extractBundle(bundleType : InjectType): List<String> = map {
//        val name = it.name
//        val type = it.type.declaration.qualifiedName!!.asString()
//        when(bundleType){
//            InjectType.Fragment -> "fragment.$name = fragment.fromBundle<${type}>(\"$name\")"
//            InjectType.Activity -> "activity.$name = activity.fromBundle<${type}>(\"$name\")"
//            InjectType.Variable -> "val $name = bundle.fromBundle<${type}>(\"$name\")"
//        }
//    }.toList()
}
