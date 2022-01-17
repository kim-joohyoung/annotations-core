package com.jhkim.annotations_core.fragment

import androidx.fragment.app.Fragment
import com.jhkim.annotations.Arg

open class BaseFragment  : Fragment() {
    @Arg
    var baseArg : Int = 0
}