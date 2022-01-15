package com.jhkim.annotations_core

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.jhkim.annotations.Extra
import com.jhkim.annotations.ActivityLauncher
import com.jhkim.annotations.Result
import com.jhkim.annotations_core.databinding.ActivitySecondBinding

@ActivityLauncher
@Result("result1", String::class)
@Result("result2", String::class)
class SecondActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySecondBinding

    @Extra
    var arg1 : String = ""
    @Extra
    var arg2 : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)
        SecondActivityLauncher.inject(this)
        binding.textView.text = arg1
        binding.textView2.text = arg2
        binding.close.setOnClickListener {
            SecondActivityLauncher.setResult(this, RESULT_OK, "Successes", "Successes")
            finish()
        }
    }
}
