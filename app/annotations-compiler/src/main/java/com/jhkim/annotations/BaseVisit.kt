package com.jhkim.annotations

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSVisitorVoid
import java.io.OutputStream

abstract class BaseVisit(private val codeGenerator: CodeGenerator, private val logger: KSPLogger) : KSVisitorVoid() {
    fun getFile(classDeclaration: KSClassDeclaration, className:String): OutputStream? {
        return try{
            val packageName = classDeclaration.packageName.asString()
            codeGenerator.createNewFile(
                dependencies = Dependencies(true, classDeclaration.containingFile!!),
                packageName = packageName,
                fileName = className
            )
        }catch (ex:FileAlreadyExistsException){
            null
        }
    }
}