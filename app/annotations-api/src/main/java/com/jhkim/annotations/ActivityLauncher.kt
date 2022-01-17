package com.jhkim.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class ActivityLauncher(val checkSuperClass : Boolean = false)
