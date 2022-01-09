package com.jhkim.annotations

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration

class EActivityVisitor(codeGenerator: CodeGenerator, private val logger: KSPLogger) : BaseVisit(codeGenerator, logger) {
    override fun visitClassDeclaration(classDeclaration: KSClassDeclaration, data: Unit) {
        val className = "${classDeclaration.simpleName.asString()}Builder"
        val file = getFile(classDeclaration, className) ?: return
        file.append(EActivityBuilder(classDeclaration, logger).makeBuilderFile())
        file.close()
    }
}

class EActivityBuilder(private val classDeclaration: KSClassDeclaration, private val logger: KSPLogger) {
    private val args = classDeclaration.getProperties(Extra::class.java)
    private val packageName = classDeclaration.packageName.asString()

    fun makeBuilderFile() : String {
        val className = classDeclaration.simpleName.asString()
        logger.warn("Build $className")
        return """package $packageName

import android.app.Activity
import android.content.Intent
import android.content.Context
import androidx.core.os.bundleOf

class ${className}Builder(${args.argString()}){
    fun intent(context:Context) = Intent(context, $className::class.java).apply {
		putExtras(bundle())	
    }
    fun bundle() = bundleOf(
${args.joinString { name, _ -> "\"$name\" to $name"}}    
    )
}

fun $className.inject(){
${args.injectString("intent?.extras?")}
}
"""
    }
}
