package com.jhkim.annotations

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.Nullability


fun List<KSPropertyDeclaration>.joinString(separator: CharSequence = ", ", transform: ((String, KSType) -> CharSequence)): String {
    return joinToString(separator = separator) {
        val name = it.simpleName.asString()
        val type = it.type.resolve()
        transform(name, type)
    }
}

fun List<KSPropertyDeclaration>.argString() = joinString { name, type -> "val $name : $type" }

fun List<KSPropertyDeclaration>.paramString() = joinString { name, type -> "$name : $type" }
fun List<KSPropertyDeclaration>.injectString(bundle : String) =
    joinString("\n") { name, type ->
        when (type.nullability) {
            Nullability.NOT_NULL -> "\t$name = requireNotNull($bundle.get(\"$name\") as $type){\"$name\"}"
            else -> "\t$name = $bundle.get(\"$name\") as ${type.makeNotNullable()}"
        }
    }