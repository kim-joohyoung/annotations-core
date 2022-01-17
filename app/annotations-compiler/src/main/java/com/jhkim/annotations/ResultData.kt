package com.jhkim.annotations

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName

data class ResultData(val name : String, val type : KSType){
    constructor(arg : KSPropertyDeclaration) : this(arg.simpleName.asString(), arg.type.resolve())

    fun bundleOf() = "\"$name\" to $name"
}

fun List<ResultData>.bundleOf() = if(isEmpty()) "bundleOf()" else "bundleOf(${joinToString {it.bundleOf()}})"

fun List<ResultData>.toArgsString() =  joinToString { it.name }
fun List<ResultData>.toArgsString(vararg prefix:String) =  ( prefix.asSequence() + map { it.name }).joinToString()

@KotlinPoetKspPreview
fun List<ResultData>.toParameterSpec() = map { ParameterSpec.builder(it.name, it.type.toClassName()).build() }



fun KSAnnotated.getAnnotations() =  this.annotations.filter {
        it.shortName.getShortName() == Result::class.simpleName
    }.map { ResultData(it.arguments[0].value as String, it.arguments[1].value as KSType) }.toList()

