package com.jhkim.annotations_core

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.os.bundleOf
import com.jhkim.annotations.EActivity
import com.jhkim.annotations.Extra
import com.jhkim.annotations.Launcher
import com.jhkim.annotations.ResultExtra
import com.jhkim.annotations_core.databinding.ActivitySecondBinding

@Launcher
@EActivity
class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding

    @Extra
    var arg1 : String = ""
    @Extra
    var arg2 : String? = null

    @ResultExtra
    var result : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SecondActivityBuilder.inject(this)
        binding.textView.text = arg1
        binding.textView2.text = arg2
        binding.close.setOnClickListener {
            result = "Successes"
            SecondActivityLauncher.setResult(this, RESULT_OK)
            finish()
        }
    }
}
