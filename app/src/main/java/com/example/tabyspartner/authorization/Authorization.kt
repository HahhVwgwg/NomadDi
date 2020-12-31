package com.example.tabyspartner.authorization

import android.content.Intent
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
import com.example.tabyspartner.main.MainPageViewModel
import com.example.tabyspartner.networking.MobizonApi
import com.example.tabyspartner.networking.MobizonResponse
import com.example.tabyspartner.otp.Otp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.properties.Delegates

class Authorization : AppCompatActivity() {

    private lateinit var binding: ActivityAuthorizationBinding

    private val viewModel: AuthorizationViewModel by lazy {
        ViewModelProvider(this).get(AuthorizationViewModel::class.java)
    }


    private var isRegistered by Delegates.notNull<Boolean>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_authorization)
        binding.lifecycleOwner = this
        isRegistered = false
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
                binding.loginFormFeedback.visibility = View.INVISIBLE
                val randomOTP  = Otp().OTP(4)
                val apiKey = "kzd22a59d1901e822d4a767ef3bdb90a233d879cdb67be0dff27ecde91897e276ea46d"
                viewModel.getUser(this,"+$complete_phone_number",randomOTP)
                viewModel.response.observe(binding.lifecycleOwner as Authorization, Observer {
                  if(it.driver_profile.first_name!=null) {
                      val homeIntent = Intent(this,MobizonActivity::class.java)
                      homeIntent.putExtra("OTP",randomOTP)
                      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                      intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                      startActivity(homeIntent)
                      finish()
                  }else {
                      binding.loginFormFeedback.text = "Такого пользователя нет в списке водителей"
                      binding.loginProgressBar.visibility = View.INVISIBLE
                      binding.loginFormFeedback.visibility = View.VISIBLE
                      binding.generateBtn.isEnabled = true
                  }
                })

//                MobizonApi.retrofitService.sendMessage(recipient = complete_phone_number,text = "Ваш код подтверждения: "+randomOTP.OTP(4),apiKey = apiKey).enqueue(object : Callback<MobizonResponse> {
//                    override fun onResponse(call: Call<MobizonResponse>, response: Response<MobizonResponse>) {
//                        if(response.body()?.data?.status == 1) {
//                            val intent = Intent(this@Authorization,MobizonActivity::class.java)
//                            startActivity(intent)
//                        }
//                    }
//                    override fun onFailure(call: Call<MobizonResponse>, t: Throwable) {
//                        Log.d("Mobizon",t.message.toString())
//                    }
//                })
            }
        }

        binding.root
    }

    override fun onStart() {
        super.onStart()
//        if(isRegistered) {
//            val homeIntent = Intent(this,MainActivity::class.java)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
//            startActivity(homeIntent)
//            finish()
//        }
    }


}