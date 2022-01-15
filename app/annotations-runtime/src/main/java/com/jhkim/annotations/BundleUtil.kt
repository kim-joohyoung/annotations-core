package com.jhkim.annotations

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.app.Activity

fun Activity.containsKey(key: String) = intent.extras?.containsKey(key) ?: false 
fun Fragment.containsKey(key: String) = arguments?.containsKey(key) ?: false

inline fun <reified T> Activity.extra(key: String) = intent.extras?.get(key) as T 
inline fun <reified T> Fragment.extra(key: String) = arguments?.get(key) as T
inline fun <reified T> Bundle.extra(key: String) = get(key) as T

inline fun <reified T> Activity.extraNotNull(key: String) = requireNotNull(intent.extras?.get(key) as T) { key } 
inline fun <reified T> Fragment.extraNotNull(key: String) = requireNotNull(arguments?.get(key) as T) { key } 
inline fun <reified T> Bundle.extraNotNull(key: String) = requireNotNull(get(key) as T){key}
