package com.jhkim.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ActivityBuilder(val checkSuperClass : Boolean = false)
