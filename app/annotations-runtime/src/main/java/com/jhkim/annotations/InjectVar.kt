package com.jhkim.annotations

import android.app.Activity
import android.util.Log
import androidx.fragment.app.Fragment
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class InjectVar<T> : ReadOnlyProperty<Any, T> {
    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        return when (thisRef) {
            is Activity -> thisRef.intent.extras!!.get(property.name) as T
            is Fragment -> thisRef.requireArguments().get(property.name) as T
            else -> {
                throw IllegalArgumentException(thisRef.javaClass.simpleName)
            }
        }
//        return when {
//            Activity::class.java.isAssignableFrom(thisRef.javaClass) -> (thisRef as Activity).intent.extras!!.get(property.name) as T
//            Fragment::class.java.isAssignableFrom(thisRef.javaClass) -> (thisRef as Fragment).requireArguments().get(property.name) as T
//            else -> {
//                throw IllegalArgumentException(thisRef.javaClass.simpleName)
//            }
//        }
    }
}
