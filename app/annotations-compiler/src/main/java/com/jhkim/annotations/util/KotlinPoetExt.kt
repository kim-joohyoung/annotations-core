package com.jhkim.annotations.util

import com.google.devtools.ksp.getAllSuperTypes
import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.jhkim.annotations.ResultData
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ksp.toTypeName

fun KSClassDeclaration.isTypeOf(clz : ClassName) =
    getAllSuperTypes().firstOrNull { checkNotNull(it.declaration.qualifiedName).asString() == clz.canonicalName } != null
//fun KSClassDeclaration.isTypeOf(clz : ClassName) =
//    getAllSuperTypes().firstOrNull { it.declaration.toClassNameEx() == clz } != null
//
//fun KSDeclaration.toClassNameEx(): ClassName {
//    require(!isLocal()) {
//        "Local/anonymous classes are not supported!"
//    }
//    val pkgName = packageName.asString()
//    val typesString = checkNotNull(qualifiedName).asString().removePrefix("$pkgName.")
//
//    val simpleNames = typesString
//        .split(".")
//    return ClassName(pkgName, simpleNames)
//}
//

fun FunSpec.Builder.addStatements(vararg format : String): FunSpec.Builder = addStatements(format.toList())
fun FunSpec.Builder.addStatements(format : List<String>): FunSpec.Builder {
    format.forEach {
        addStatement(it)
    }
    return this
}

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

fun KSClassDeclaration.getAllProperties(cls:Class<*>, inherited : Boolean): List<ResultData> {
    val name = cls.simpleName
    val props =if(inherited) getAllProperties() else getDeclaredProperties()
    return props.filter { property ->
        property.annotations.find { it.shortName.getShortName() == name }!=null
    }.map { ResultData(it) }.toList()
}

fun KSClassDeclaration.hasCompanion() =
    declarations.firstOrNull { it.simpleName.asString()=="Companion"}!=null

fun KSClassDeclaration.getAnnotation(cls:Class<*>): KSAnnotation? {
    return annotations.firstOrNull { it.shortName.asString() == cls.simpleName }
}

fun KSAnnotation.argument(argName: String) = arguments.first { it.name?.asString() == argName }
@Suppress("UNCHECKED_CAST")
fun <T:Any> KSAnnotation.argumentValue(argName: String) = arguments.first { it.name?.asString() == argName }.value as T
