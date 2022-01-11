package com.jhkim.annotations.util

import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toTypeName

object ClassEx {
    val Bundle = ClassName("android.os", "Bundle")
    val BundleNullable = Bundle.copy(true)
    val Context = ClassName("android.content", "Context")
    val Intent = ClassName("android.content", "Intent")
    val IntentNullable = Intent.copy(true)

    val Fragment = ClassName("androidx.fragment.app", "Fragment")
    val Activity = ClassName("android.app", "Activity")
    val ComponentActivity = ClassName("androidx.activity", "ComponentActivity")

    //val ActivityResult = ClassName("androidx.activity.result", "ActivityResultLauncher<Bundle?>")
    val ActivityResult = ClassName("androidx.activity.result", "ActivityResultLauncher")
        .parameterizedBy(BundleNullable)
    val ActivityResultContract = ClassName("androidx.activity.result.contract", "ActivityResultContract")
        .parameterizedBy(Bundle, BundleNullable)

}

fun isTypeOf(classDeclaration: KSClassDeclaration, clz : ClassName) : Boolean{
    return isTypeOf(classDeclaration, clz.simpleName)
}

fun isTypeOf(classDeclaration: KSClassDeclaration, clz : String) : Boolean{
    if(classDeclaration.qualifiedName?.asString() == clz){
        return true
    }
    val parent = classDeclaration.superTypes.firstOrNull()?.resolve()?.declaration ?: return false
    return isTypeOf(parent as KSClassDeclaration, clz)
}

fun FunSpec.Builder.addStatements(vararg format : String): FunSpec.Builder = addStatements(format.toList())
fun FunSpec.Builder.addStatements(format : List<String>): FunSpec.Builder {
    format.forEach {
        addStatement(it)
    }
    return this
}

@KotlinPoetKspPreview
fun List<KSPropertyDeclaration>.toParameterSpec() = map { ParameterSpec(it.simpleName.asString(), it.type.toTypeName()) }

fun List<KSPropertyDeclaration>.toBundleStrings(varPrefix : String = "", target: String="bundle") = map { it.toBundleString(varPrefix, target)  }

fun List<KSPropertyDeclaration>.toArgsString() = joinToString { it.simpleName.asString() }

fun List<KSPropertyDeclaration>.toBundleOf() = "bundleOf(${joinToString { "\"${it.simpleName.asString()}\" to ${it.simpleName.asString()}" }})"

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