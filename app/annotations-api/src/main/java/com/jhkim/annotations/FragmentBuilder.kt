package com.jhkim.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class FragmentBuilder(val checkSuperClass : Boolean = false, val listener :Boolean = false)
