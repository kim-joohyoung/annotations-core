package com.jhkim.annotations

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract

class ActivityContract(private val cls : Class<*>)  : ActivityResultContract<Bundle, Bundle?>() {
    override fun createIntent(context: Context, input: Bundle): Intent = Intent(context,
        cls).also { it.putExtras(input) }

    override fun parseResult(resultCode: Int, intent: Intent?): Bundle? = intent?.extras
}