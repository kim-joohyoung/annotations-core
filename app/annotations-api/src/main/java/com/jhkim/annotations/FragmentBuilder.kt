package com.jhkim.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.SOURCE)
annotation class FragmentBuilder(val listener :Boolean = false) {

}