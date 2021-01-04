package com.example.tabyspartner.pin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.tabyspartner.MainActivity
import com.example.tabyspartner.R
import com.example.tabyspartner.authorization.Authorization
import com.example.tabyspartner.databinding.ActivityVerificationBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class VerificationActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    var pinCode = ""
    var isPinCodeCreated = false
    private lateinit var binding: ActivityVerificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verification)
        binding.lifecycleOwner = this

        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        isPinCodeCreated = sharedPreferences.getBoolean("USER_PIN_CODE_CREATED",false)
        pinCode = sharedPreferences.getString("USER_PIN_CODE","")!!

        binding.root
    }


    override fun onResume() {
        super.onResume()
        if(isPinCodeCreated) {
            binding.tvInputTip.text = "Введите ваш код доступа"
            binding.pinCodeVerifyText.visibility = View.GONE
            binding.pinCodeText.hint = "Введите код"
            binding.forgetPassword.visibility = View.VISIBLE
            binding.forgetPassword.setOnClickListener {
                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.alert_password_recover_title))
                    .setMessage(resources.getString(R.string.alert_password_recover_message))
                    .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                        // Respond to negative button press
                        dialog.dismiss()
                    }
                    .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                        // Respond to positive button press
                        sharedPreferences.edit().clear().apply()
                        startActivity(Intent(this,Authorization::class.java))
                        finish()
                    }
                    .show()
            }
            binding.verifyPinCodeBtn.setOnClickListener {
                if(binding.pinCodeText.text.trim().isEmpty()) {
                    binding.verifyCodeFeedBack.text = "Поле не должно быть пустым"
                    binding.verifyCodeFeedBack.visibility = View.VISIBLE
                }else if(binding.pinCodeText.text.trim().toString() != pinCode) {
                    binding.verifyCodeFeedBack.text = "Попробуйте еще раз"
                    binding.verifyCodeFeedBack.visibility = View.VISIBLE
                } else if (binding.pinCodeText.text.trim().toString() == pinCode){
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }
            }
        }else {
            binding.verifyPinCodeBtn.setOnClickListener {
                if(binding.pinCodeText.text.trim().isEmpty() || binding.pinCodeVerifyText.text.trim().isEmpty()) {
                    binding.verifyCodeFeedBack.text = "Поле не должно быть пустым"
                    binding.verifyCodeFeedBack.visibility = View.VISIBLE
                }else if(binding.pinCodeText.text.length != 4) {
                    binding.verifyCodeFeedBack.text = "Код должен состоять из 4 цифр"
                    binding.verifyCodeFeedBack.visibility = View.VISIBLE
                } else if(binding.pinCodeText.text.trim() != binding.pinCodeVerifyText.text.trim()){
                    binding.verifyCodeFeedBack.text = "Код не совпадает"
                    binding.verifyCodeFeedBack.visibility = View.VISIBLE
                } else {
                    binding.verifyCodeFeedBack.visibility = View.INVISIBLE
                    sharedPreferences.edit()
                        .putString("USER_PIN_CODE",binding.pinCodeText.text.trim().toString())
                        .putBoolean("USER_PIN_CODE_CREATED",true)
                        .apply()
                    Toast.makeText(this,"Пинкод создан",Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this,MainActivity::class.java))
                    finish()
                }
            }
        }

    }
}