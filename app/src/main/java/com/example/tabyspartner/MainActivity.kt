package com.example.tabyspartner

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toolbar
import androidx.appcompat.app.ActionBar
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.tabyspartner.databinding.ActivityMainBinding
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_main)
        val toolbar = binding.toolbar
        setSupportActionBar(toolbar)

        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when(item.itemId) {
                R.id.main_menu_item -> {
                    // Respond to navigation item 1 click
                    true
                }
                R.id.withdraw_menu_item -> {
                    // Respond to navigation item 2 click
                    true
                }
                R.id.support_menu_item -> {
                    true
                }
                R.id.profile_menu_item -> {
                    true
                }
                else -> false
            }
        }
    }

    private fun handleFrame(fragment: Fragment) {

    }
}