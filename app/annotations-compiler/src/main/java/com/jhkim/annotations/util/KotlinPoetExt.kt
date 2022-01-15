package com.jhkim.annotations.util

import com.google.devtools.ksp.*
import com.google.devtools.ksp.symbol.*
import com.jhkim.annotations.ResultData
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.KotlinPoetKspPreview
import com.squareup.kotlinpoet.ksp.toClassName
import com.squareup.kotlinpoet.ksp.toTypeName

fun KSClassDeclaration.isTypeOf(clz : ClassName) =
    getAllSuperTypes().firstOrNull { it.declaration.toClassNameEx() == clz } != null

fun KSDeclaration.toClassNameEx(): ClassName {
    require(!isLocal()) {
        "Local/anonymous classes are not supported!"
    }
    val pkgName = packageName.asString()
    val typesString = checkNotNull(qualifiedName).asString().removePrefix("$pkgName.")

    val simpleNames = typesString
        .split(".")
    return ClassName(pkgName, simpleNames)
}


fun FunSpec.Builder.addStatements(vararg format : String): FunSpec.Builder = addStatements(format.toList())
fun FunSpec.Builder.addStatements(format : List<String>): FunSpec.Builder {
    format.forEach {
        addStatement(it)
    }
    return this
}


@JvmName("toParameterSpecResultData")
@KotlinPoetKspPreview
fun List<ResultData>.toParameterSpec() = map { ParameterSpec.builder(it.name, it.type.toClassName()).build() }

@KotlinPoetKspPreview
fun ArrayList<KSType>.toParameterSpec() = mapIndexed { index, type ->  ParameterSpec.builder("param$index", type.toClassName()).build() }

@KotlinPoetKspPreview
fun List<KSPropertyDeclaration>.toParameterSpec() = map { ParameterSpec(it.simpleName.asString(), it.type.toTypeName()) }

@KotlinPoetKspPreview
fun TypeSpec.Builder.primaryConstructor(args : List<ResultData>) =
    this.primaryConstructor(
        FunSpec.constructorBuilder()
            .addParameters(args.map {
                ParameterSpec(
                    it.name,
                    it.type.toTypeName()
                )
            })
            .build()
    )
    .addProperties(
        args.map {
            PropertySpec.builder(it.name, it.type.toTypeName())
                .initializer(it.name)
                .build()
        }
    )


fun KSClassDeclaration.getProperties(cls:Class<*>): List<ResultData> {
    val name = cls.simpleName
    return getDeclaredProperties().filter { property ->
        property.annotations.find { it.shortName.getShortName() == name }!=null
    }.map { ResultData(it) }.toList()
}

fun KSClassDeclaration.hasCompanion() =
    declarations.firstOrNull { it.simpleName.asString()=="Companion"}!=null

fun KSClassDeclaration.getAnnotation(cls:Class<*>): KSAnnotation? {
    return annotations.firstOrNull { it.shortName.asString() == cls.simpleName }
}


fun KSClassDeclaration. getAnnotationArg(cls:Class<*>, argName: String): KSValueArgument? {
    return this.getAnnotation(cls)?.arguments?.firstOrNull { it.name?.asString() == argName }
}

@Suppress("UNCHECKED_CAST")
fun <T> KSClassDeclaration.getAnnotationArgValue(cls:Class<*>, argName: String): T? {
    return getAnnotationArg(cls, argName)?.value as T
}
