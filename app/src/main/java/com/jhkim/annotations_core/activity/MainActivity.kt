package com.jhkim.annotations_core.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.navigation.ui.AppBarConfiguration
import com.jhkim.annotations.*
import com.jhkim.annotations_core.R
import com.jhkim.annotations_core.databinding.ActivityMainBinding
import com.jhkim.annotations_core.fragment.FirstFragmentBuilder
import com.jhkim.annotations_core.model.EnumTest

@ActivityBuilder
class MainActivity : AppCompatActivity() {
//    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val launcher = SecondActivityLauncher()

    @Extra
    var arg1 = EnumTest.Test1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        launcher.register(this)

    supportFragmentManager.commit {
        replace(R.id.layout, FirstFragmentBuilder.build(10))
    }
        binding.fab.setOnClickListener {
            launcher.launch("arg1", "arg2", EnumTest.Test1){ result1, result2 ->
                Toast.makeText(this, "result1=$result1\nresult2=$result2", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}