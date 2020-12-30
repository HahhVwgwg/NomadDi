package com.example.tabyspartner.authorization

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.tabyspartner.MainActivity
import com.example.tabyspartner.R
import com.example.tabyspartner.databinding.ActivityMobizonBinding

class MobizonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMobizonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mobizon)
        binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.verifyBtn.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
    }
}