package com.example.tabyspartner.ui.ui.authorization

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.tabyspartner.MainActivity
import com.example.tabyspartner.R
import com.example.tabyspartner.databinding.ActivityMobizonBinding
import com.example.tabyspartner.ui.ui.main.MainPageFragment
import com.example.tabyspartner.ui.ui.pin.VerificationActivity

class MobizonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMobizonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mobizon)
        binding.root
    }

    override fun onResume() {
        super.onResume()
        val otp: String? = intent.getStringExtra("verCode")
        val phone: String? = intent.getStringExtra("phoneNumber")
        binding.verifyBtn.setOnClickListener {
            binding.otpProgressBar.visibility = View.VISIBLE
            if(binding.otpTextView.text.toString() == otp) {
                startActivity(Intent(this, VerificationActivity()::class.java))
                finish()
            }else {
                binding.otpFormFeedback.text = "Код неверный убедитесь что вы ввели правильный код"

            }
        }
    }
}