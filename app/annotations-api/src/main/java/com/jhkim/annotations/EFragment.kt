package com.jhkim.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class EFragment(val buildNewInstance :Boolean = false) {

}
