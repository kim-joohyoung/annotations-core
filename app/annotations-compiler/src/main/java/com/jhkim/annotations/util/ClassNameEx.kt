package com.jhkim.annotations.util

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy

object ClassNameEx {
    val Bundle = ClassName("android.os", "Bundle")
    val BundleNullable = Bundle.copy(true)
    val Context = ClassName("android.content", "Context")
    val Intent = ClassName("android.content", "Intent")
    val IntentNullable = Intent.copy(true)

    val Fragment = ClassName("androidx.fragment.app", "Fragment")
    val Activity = ClassName("android.app", "Activity")
    val ComponentActivity = ClassName("androidx.activity", "ComponentActivity")
    val FragmentManager = ClassName("androidx.fragment.app", "FragmentManager")
    val LifecycleOwner = ClassName("androidx.lifecycle", "LifecycleOwner")

    //val ActivityResult = ClassName("androidx.activity.result", "ActivityResultLauncher<Bundle?>")
    val ActivityResult = ClassName("androidx.activity.result", "ActivityResultLauncher")
        .parameterizedBy(BundleNullable)
    val ActivityResultContract = ClassName(
        "androidx.activity.result.contract",
        "ActivityResultContract"
    )
        .parameterizedBy(Bundle, BundleNullable)

}