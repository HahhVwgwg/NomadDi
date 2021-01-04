package com.example.tabyspartner.ui.ui.authorization

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.tabyspartner.MainActivity
import com.example.tabyspartner.R
import com.example.tabyspartner.databinding.ActivityAuthorizationBinding
import com.example.tabyspartner.main.AuthorizationViewModel
import com.example.tabyspartner.ui.ui.main.MainPageViewModel
import com.example.tabyspartner.networking.MobizonApi
import com.example.tabyspartner.networking.MobizonResponse
import com.example.tabyspartner.ui.ui.otp.Otp
import com.example.tabyspartner.ui.ui.pin.VerificationActivity
import com.example.tabyspartner.prefs.PreferencesManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class Authorization : AppCompatActivity() {

    private lateinit var binding: ActivityAuthorizationBinding

    private val viewModel: AuthorizationViewModel by lazy {
        ViewModelProvider(this).get(AuthorizationViewModel::class.java)
    }

    lateinit var sharedPreferences: SharedPreferences
    var isRegistered = false
    var phoneNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_authorization)
        binding.lifecycleOwner = this

        sharedPreferences = getSharedPreferences("app_prefs",Context.MODE_PRIVATE)

        isRegistered = sharedPreferences.getBoolean("USER_REGISTERED",false)
        phoneNumber = sharedPreferences.getString("USER_PHONE_NUMBER","")!!


        if(isRegistered) {
            val intent = Intent(this, VerificationActivity::class.java)
            startActivity(intent)
            finish()
        }else {
            binding.generateBtn.setOnClickListener {
                val phone_number =  binding.phoneNumberText.text.toString()
                val complete_phone_number =
                        binding.countryCodeText.text.subSequence(1,binding.countryCodeText.text.length).toString()+
                                "" + binding.phoneNumberText.text.toString()
                if(phone_number.trim().isEmpty()) {
                    binding.loginFormFeedback.text = "Пожалуйста введите номер телефона"
                    binding.loginFormFeedback.visibility = View.VISIBLE
                }else if(phone_number.trim().length != 10){
                    binding.loginFormFeedback.text = "Пожалуйста введите корректный номер xxx-xxx-xx-xx"
                    binding.loginFormFeedback.visibility = View.VISIBLE
                } else {
                    binding.loginProgressBar.visibility = View.VISIBLE
                    binding.generateBtn.isEnabled = false
                    //binding.loginFormFeedback.visibility = View.VISIBLE
                    viewModel.getUser("+$complete_phone_number")
                    viewModel.response.observe(binding.lifecycleOwner as Authorization, Observer {
                        //println(it.driver_profile.phones[0])
                        if(it.driver_profile.phones[0]=="+${complete_phone_number}") {
                            sharedPreferences.edit()
                                    .putString("USER_PHONE_NUMBER","+${complete_phone_number}")
                                    .putBoolean("USER_REGISTERED",true)
                                    .apply()
                            viewModel.getMessageStatus(this,"+$complete_phone_number")
                            viewModel.responseOtp.observe(binding.lifecycleOwner as Authorization, Observer {
                                val intent = Intent(this, MobizonActivity::class.java)
                                intent.putExtra("phoneNumber", "+$complete_phone_number")
                                intent.putExtra("verCode",it)
                                startActivity(intent)
                                finish()
                            })
                        }
                        else {
                            binding.loginFormFeedback.text = "Такого пользователя нет в списке водителей"
                            binding.loginFormFeedback.visibility = View.VISIBLE
                        }
                    })
                }
            }
        }
        binding.root
    }



    override fun onStart() {
        super.onStart()
    }


}