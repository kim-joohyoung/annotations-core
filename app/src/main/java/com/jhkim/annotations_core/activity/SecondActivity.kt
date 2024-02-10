package com.jhkim.annotations_core.activity

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jhkim.annotations.ActivityLauncher
import com.jhkim.annotations.Extra
import com.jhkim.annotations.InjectVar
import com.jhkim.annotations.Result
import com.jhkim.annotations_core.databinding.ActivitySecondBinding
import com.jhkim.annotations_core.model.EnumTest

@ActivityLauncher
@Result("result1", String::class)
@Result("result2", String::class)
class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding

    @Extra
    var arg1 : String = ""

    @delegate:Extra
    val arg2 : String by InjectVar()
    @Extra
    var arg3 : EnumTest = EnumTest.Test1

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SecondActivityLauncher.inject(this)
        binding.textView.text = "arg1 = $arg1"
        binding.textView2.text = "arg2 = $arg2"
        binding.close.setOnClickListener {
            SecondActivityLauncher.setResult(this, RESULT_OK, "result1", "result2")
            finish()
        }
    }
}
