package com.jhkim.annotations

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType

data class ResultData(val name : String, val type : KSType){
    constructor(arg : KSPropertyDeclaration) : this(arg.simpleName.asString(), arg.type.resolve())
}


fun KSAnnotated.getAnnotations() =  this.annotations.filter {
        it.shortName.getShortName() == Result::class.simpleName
    }.map { ResultData(it.arguments[0].value as String, it.arguments[1].value as KSType) }.toList()

