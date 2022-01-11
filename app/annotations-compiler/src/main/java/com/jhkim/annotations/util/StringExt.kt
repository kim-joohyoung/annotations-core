package com.jhkim.annotations.util

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


fun KSPropertyDeclaration.toBundleString(varPrefix : String = "", target: String="bundle"): String {
    val name = simpleName.asString()
    val type = type.resolve()

    return when (type.nullability) {
        Nullability.NOT_NULL -> "$varPrefix$name = requireNotNull($target.get(\"$name\") as $type){\"$name\"}"
        else -> "$varPrefix$name = $target.get(\"$name\") as $type"
    }
}