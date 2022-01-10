package com.jhkim.annotations.util

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toTypeName

object ClassEx {
    val Bundle = ClassName("android.os", "Bundle")
    val Context = ClassName("android.content", "Context")
    val Intent = ClassName("android.content", "Intent")

}

fun FunSpec.Builder.addStatements(vararg statement : String): FunSpec.Builder {
    statement.forEach {
        addStatement(it)
    }
    return this
}

@KotlinPoetKspPreview
fun TypeSpec.Builder.primaryConstructor(args : List<KSPropertyDeclaration>) =
    this.primaryConstructor(
        FunSpec.constructorBuilder()
            .addParameters(args.map {
                ParameterSpec(
                    it.simpleName.asString(),
                    it.type.toTypeName()
                )
            })
            .build()
    )
    .addProperties(
        args.map {
            PropertySpec.builder(it.simpleName.asString(), it.type.toTypeName())
                .initializer(it.simpleName.asString())
                .build()
        }
    )