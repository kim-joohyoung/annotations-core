package com.jhkim.annotations.base

import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.symbol.KSClassDeclaration

class BaseBuilder(private val classDeclaration: KSClassDeclaration, private val logger: KSPLogger) {
}