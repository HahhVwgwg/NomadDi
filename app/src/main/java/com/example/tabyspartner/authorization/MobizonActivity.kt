package com.example.tabyspartner.authorization

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
        val ss: String? = intent.getStringExtra("OTP")
        binding.verifyBtn.setOnClickListener {
            if(binding.otpTextView.text.toString() == ss) {
                startActivity(Intent(this,MainActivity()::class.java))
            }else {
                binding.otpFormFeedback.text = "Код неверный убедитесь что вы ввели неверно"
                binding.otpFormFeedback.visibility = View.VISIBLE
            }
        }
    }
}