package com.example.tabyspartner.ui.ui.authorization

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.tabyspartner.MainActivity
import com.example.tabyspartner.R
import com.example.tabyspartner.databinding.ActivityAuthorizationBinding
import com.example.tabyspartner.main.AuthorizationViewModel
import com.example.tabyspartner.ui.ui.pin.VerificationActivity


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
        checkConnectivity()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_authorization)
        binding.lifecycleOwner = this

        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        isRegistered = sharedPreferences.getBoolean("USER_REGISTERED", false)
        phoneNumber = sharedPreferences.getString("USER_PHONE_NUMBER", "")!!


        if(isRegistered) {
            val intent = Intent(this, VerificationActivity::class.java)
            startActivity(intent)
            finish()
        }else {
            binding.generateBtn.setOnClickListener {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.mainLayoutAuthorization.getWindowToken(), 0)
                val phone_number =  binding.phoneNumberText.text.toString()
                val complete_phone_number =
                        binding.countryCodeText.text.subSequence(
                            1,
                            binding.countryCodeText.text.length
                        ).toString()+
                                "" + binding.phoneNumberText.text.toString()

                if(phone_number.trim().isEmpty()) {
                    binding.loginFormFeedback.text = "Пожалуйста введите номер телефона"
                    binding.loginFormFeedback.visibility = View.VISIBLE
                }else if(phone_number.trim().length != 10){
                    binding.loginFormFeedback.text = "Пожалуйста введите корректный номер xxx-xxx-xx-xx"
                    binding.loginFormFeedback.visibility = View.VISIBLE
                } else {
                    //binding.loginProgressBar.visibility = View.VISIBLE
                    //binding.generateBtn.isEnabled = false
                    //binding.loginFormFeedback.visibility = View.VISIBLE
                    viewModel.getUser("+$complete_phone_number",this)
                    viewModel.response.observe(binding.lifecycleOwner as Authorization, Observer {
                        //Log.d("Check",it.toString())
                        if ("+${complete_phone_number}" == it.driver_profile.phones[0]) {
                            //Log.d("Check",it.toString())
                            Log.d("CheckPhoneNUmbers","+${complete_phone_number}"+" "+it.driver_profile.phones[0])
                            sharedPreferences.edit()
                                .putString("USER_PHONE_NUMBER", "+${complete_phone_number}")
                                .putBoolean("USER_REGISTERED", true)
                                .apply()
                            viewModel.getMessageStatus(this, "+$complete_phone_number")

                            viewModel.responseOtp.observe(
                                binding.lifecycleOwner as Authorization,
                                Observer {
                                    Log.d("CheckVerCode",it.toString())
                                    val intent = Intent(this, MobizonActivity::class.java)
                                    intent.putExtra("phoneNumber", "+$complete_phone_number")
                                    intent.putExtra("verCode", it)
                                    startActivity(intent)
                                    finish()
                                })
                        } else {
                            //Log.d("Check","+${complete_phone_number}"+" "+it.driver_profile.phones[0])
                            binding.generateBtn.isEnabled = true
                            binding.loginFormFeedback.visibility = View.VISIBLE
                            binding.loginFormFeedback.text =
                                " Данный номер не зарегистрирован в нашей базе. Попробуйте еще раз."
                        }
                    })
                }
            }
        }
        binding.root
    }

    private fun checkConnectivity() {
        val manager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = manager.activeNetworkInfo

        if (null == activeNetwork) {
            val dialogBuilder = androidx.appcompat.app.AlertDialog.Builder(this)
            val intent = Intent(this, MainActivity::class.java)
            // set message of alert dialog
            dialogBuilder.setMessage(R.string.internet_message)
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton(R.string.retry, DialogInterface.OnClickListener { dialog, id ->
                    recreate()
                })
                // negative button text and action
                .setNegativeButton(R.string.otmenit, DialogInterface.OnClickListener { dialog, id ->
                    finish()
                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle(R.string.internet_title)
            // show alert dialog
            alert.show()
        }
    }

    override fun onStart() {
        super.onStart()
    }


}