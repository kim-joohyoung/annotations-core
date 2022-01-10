package com.jhkim.annotations

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Nullability
import java.lang.Thread.yield


fun List<KSPropertyDeclaration>.joinString(separator: CharSequence = ", ", transform: ((String, KSType) -> CharSequence)): String {
    return joinToString(separator = separator) {
        val name = it.simpleName.asString()
        val type = it.type.resolve()
        transform(name, type)
    }
}
//
//fun List<KSPropertyDeclaration>.argString() = joinString { name, type -> "val $name : $type" }
//
//fun List<KSPropertyDeclaration>.paramString() = joinString { name, type -> "$name : $type" }
//fun List<KSPropertyDeclaration>.injectString(bundleObject : String) =
//    joinString("\n") { name, type ->
//        when (type.nullability) {
//            Nullability.NOT_NULL -> "\t$name = requireNotNull($bundleObject.get(\"$name\") as $type){\"$name\"}"
//            else -> "\t$name = $bundleObject.get(\"$name\") as ${type.makeNotNullable()}"
//        }
//    }

fun List<KSPropertyDeclaration>.mapString(transform: ((String, KSType) -> String)) = sequence {
    this@mapString.forEach {
        val name = it.simpleName.asString()
        val type = it.type.resolve()
        yield(transform(name, type))
    }
}

fun List<KSPropertyDeclaration>.getInjectString(bundleObject : String): List<String> {
    return this.mapString { name, type ->
        injectString(bundleObject, name, type)
    }.toList()
}

fun injectString(bundleObject: String, name : String, type : KSType): String {
    return when (type.nullability) {
        Nullability.NOT_NULL -> "$name = requireNotNull($bundleObject.get(\"$name\") as $type){\"$name\"}"
        else -> "$name = $bundleObject.get(\"$name\") as ${type.makeNotNullable()}"
    }
}