package com.jhkim.annotations.util

import com.google.devtools.ksp.symbol.KSPropertyDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.jhkim.annotations.ResultData
import java.util.*

fun List<ResultData>.bundleOf() = "bundleOf(${joinToString {"\"${it.name}\" to ${it.name}"}})"

fun List<ResultData>.toArgsString() = joinToString { it.name }

//fun Sequence<ResultData>.getBundleList(bundle: String) = map { "var ${it.name} = $bundle.get(\"${it.name}\") as ${it.type}" }.toList()
//fun ArrayList<KSType>.getBundleList(bundle: String) = mapIndexed { index, type -> "var param$index = $bundle.get(\"param$index\") as $type"}
//fun List<KSPropertyDeclaration>.getBundleList(varPrefix : String = "", target: String="bundle") = map { it.getBundleList(varPrefix, target)  }
//
//fun KSPropertyDeclaration.getBundleList(varPrefix : String = "", target: String="bundle"): String {
//    return getBundleList(simpleName.asString(), type.resolve(),varPrefix, target)
//}
//
//private fun getBundleList(name : String, type : KSType, varPrefix : String = "", target: String="bundle"): String {
//    return "$varPrefix$name = $target.get(\"$name\") as $type"
//}