package com.jhkim.annotations_core.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import com.jhkim.annotations.ActivityBuilder
import com.jhkim.annotations.ActivityLauncher
import com.jhkim.annotations.Extra
import com.jhkim.annotations.FragmentBuilder
import com.jhkim.annotations_core.R
import com.jhkim.annotations_core.databinding.ActivityMainBinding
import com.jhkim.annotations_core.fragment.FirstFragmentBuilder

@ActivityBuilder
class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    private val launcher = SecondActivityLauncher()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        launcher.register(this)

    supportFragmentManager.commit {
        replace(R.id.layout, FirstFragmentBuilder("Test First Fragment").build())
    }
        binding.fab.setOnClickListener {
            launcher.launch("test", "tes2"){result1, result2 ->
                Toast.makeText(this, "$result1, $result2", Toast.LENGTH_SHORT).show()
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