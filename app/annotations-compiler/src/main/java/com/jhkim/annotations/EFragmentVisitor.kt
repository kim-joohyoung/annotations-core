package com.jhkim.annotations

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration

class EFragmentVisitor(codeGenerator: CodeGenerator, private val logger: KSPLogger) : BaseVisit(codeGenerator, logger) {
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        val className = "${classDeclaration.simpleName.asString()}Builder"
        val file = getFile(classDeclaration, className) ?: return
        val builder = EFragmentBuilder(classDeclaration, logger)
        file.append(builder.makeBuilderFile())
        if(classDeclaration.hasCompanion())
            file.append(builder.makeCompanion())
        file.close()
    }
}

class EFragmentBuilder(classDeclaration: KSClassDeclaration, private val logger: KSPLogger) {
    private val args = classDeclaration.getProperties(Arg::class.java)
    private val packageName = classDeclaration.packageName.asString()
    private val className = classDeclaration.simpleName.asString()

    fun makeBuilderFile() : String {
        logger.warn("Build $className")
        return """package $packageName

import android.app.Activity
import android.content.Intent
import android.content.Context
import androidx.core.os.bundleOf
import android.os.Bundle

class ${className}Builder(${args.argString()}){
    fun build() = $className().apply {
        arguments = bundle()
    }
    fun bundle() = bundleOf(
${args.joinString { name, _ -> "\"$name\" to $name" }}
    )
}

fun $className.inject(){
${args.injectString("arguments?")}
}   
"""
    }

    fun makeCompanion() = """
fun $className.Companion.newInstance(${args.paramString()}) =
    FirstFragmentBuilder(${args.joinString { name, _ ->name}}).build()
"""
}

/*

class EFragmentBuilder(private val classDeclaration: KSClassDeclaration, private val logger: KSPLogger) {
    private val args = classDeclaration.getProperties(Arg::class.java)
    private val optionals = classDeclaration.getProperties(ArgOptional::class.java)
    private val packageName = classDeclaration.packageName.asString()

    fun makeBuilderFile() {
        val className = "${classDeclaration.simpleName.asString()}Builder"
        logger.warn("Build $className")

        file.append(makeImportString())
        file.append(makeBuilderString())
        file.append(makeInjectString())

        if(classDeclaration.hasCompanion()){
            file.append(makeCompanionString())
        }
        file.close()
    }

    private fun makeCompanionString(): String {
        val className = "${classDeclaration.simpleName.asString()}Builder"
        return """
fun ${classDeclaration.simpleName.asString()}.Companion.newInstance(${args.paramString()}) : ${classDeclaration.simpleName.asString()}{
   return $className(${args.joinString { name, _ -> name }}).build()
}
"""
    }

    private fun makeImportString(): String {
        return """package $packageName

import android.app.Activity
import android.content.Intent
import android.content.Context
import androidx.core.os.bundleOf
import android.os.Bundle
"""
    }

    private fun makeBuilderString(): String {
        val className = "${classDeclaration.simpleName.asString()}Builder"
        val opts = if(optionals.isNotEmpty()) {
            String.format("\n%s\n\n%s\n",
                optionals.joinString("\n") { name, type -> "\tlateinit var _$name:$type" },
                optionals.joinString("\n") { name, type -> "\tfun $name($name:$type){_$name=$name}" }
            )
        }
        else
            ""

        return """
class $className(${args.joinString{ name, type -> "var $name : $type" }}){$opts
    fun build() : ${classDeclaration.simpleName.asString()} {
        val pairs = mutableListOf<Pair<String, Any?>>(
${args.joinString(",\n"){ name, _ -> "\t\t\t\"$name\" to $name" }}
        )
${optionals.joinString("\n"){ name, _ ->"\t\tif(::_$name.isInitialized) pairs.add(\"$name\" to _$name)" }}
        return ${classDeclaration.simpleName.asString()}().apply{
            arguments = bundleOf(*pairs.toTypedArray())
        }
    }
}
"""
    }

    private fun makeInjectString(): String {
        return """
fun ${classDeclaration.simpleName.asString()}.inject(){
${args.joinString("\n"){ name, type ->
            when(type.nullability){
                Nullability.NOT_NULL -> "\t$name = requireNotNull(arguments?.get(\"$name\") as $type){\"$name\"}"
                else -> "\t$name = arguments?.get(\"$name\") as ${type.makeNotNullable()}"
            }
        }}
${optionals.joinString("\n"){ name, type -> "\tif(arguments?.get(\"$name\") != null) $name = arguments?.get(\"$name\") as $type"}}
}
"""
    }
}
*/
