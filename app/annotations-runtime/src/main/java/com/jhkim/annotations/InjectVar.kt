package com.jhkim.annotations

import android.app.Activity
import androidx.fragment.app.Fragment
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST", "DEPRECATION")
class InjectVar<T>(private val default: T) : ReadOnlyProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return when (thisRef) {
            is Activity -> thisRef.intent.extras?.get(property.name) as T ?: default
            is Fragment -> thisRef.arguments?.get(property.name) as T ?: default
            else -> {
                throw IllegalArgumentException(thisRef.javaClass.simpleName)
            }
        }
    }
}
