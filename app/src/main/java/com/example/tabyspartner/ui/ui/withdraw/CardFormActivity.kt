package com.example.tabyspartner.ui.ui.withdraw

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.tabyspartner.R
import com.example.tabyspartner.databinding.ActivityCardFormBinding

class CardFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardFormBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_card_form)
        binding.root
    }
}