package com.jhkim.annotations

import com.google.devtools.ksp.getDeclaredProperties
import com.google.devtools.ksp.symbol.KSAnnotation
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSValueArgument

fun isTypeOf(classDeclaration: KSClassDeclaration, clz : String) : Boolean{
    if(classDeclaration.qualifiedName?.asString() == clz){
        return true
    }
    val parent = classDeclaration.superTypes.firstOrNull()?.resolve()?.declaration ?: return false
    return isTypeOf(parent as KSClassDeclaration, clz)
}

fun KSClassDeclaration.getProperties(cls:Class<*>): List<KSPropertyDeclaration> {
    val name = cls.simpleName
    return getDeclaredProperties().filter { property ->
        property.annotations.find { it.shortName.getShortName() == name }!=null
    }.toList()
}

fun KSClassDeclaration.hasCompanion() =
    declarations.firstOrNull { it.simpleName.asString()=="Companion"}!=null

fun KSClassDeclaration.getAnnotation(cls:Class<*>): KSAnnotation? {
    return annotations.firstOrNull { it.shortName.asString() == cls.simpleName }
}

fun KSClassDeclaration.getAnnotationArg(cls:Class<*>, argName: String): KSValueArgument? {
    return this.getAnnotation(cls)?.arguments?.firstOrNull { it.name?.asString() == argName }
}

@Suppress("UNCHECKED_CAST")
fun <T> KSClassDeclaration.getAnnotationArgValue(cls:Class<*>, argName: String): T? {
    return getAnnotation(cls)?.arguments?.firstOrNull { it.name?.asString() == argName }?.value as T
}
