package com.dataplus.tabyspartner.ui.ui.authorization

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
import com.dataplus.tabyspartner.MainActivity
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.databinding.ActivityAuthorizationBinding
import com.dataplus.tabyspartner.main.AuthorizationViewModel
import com.dataplus.tabyspartner.ui.ui.pin.VerificationActivity
import kotlinx.android.synthetic.main.activity_authorization.*


class Authorization : AppCompatActivity() {

    //private lateinit var binding: ActivityAuthorizationBinding

    private val viewModel: AuthorizationViewModel by lazy {
        ViewModelProvider(this).get(AuthorizationViewModel::class.java)
    }

    lateinit var sharedPreferences: SharedPreferences
    var isRegistered = false
    var phoneNumber = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_authorization)
        checkConnectivity()


        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        isRegistered = sharedPreferences.getBoolean("USER_REGISTERED", false)
        phoneNumber = sharedPreferences.getString("USER_PHONE_NUMBER", "")!!


        if (isRegistered) {
            val intent = Intent(this, VerificationActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            generate_btn.setOnClickListener {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(mainLayoutAuthorization.getWindowToken(), 0)
                val phone_number = phone_number_text.text.toString()
                val complete_phone_number =
                    country_code_text.text.subSequence(
                        1,
                        country_code_text.text.length
                    ).toString() +
                            "" + phone_number_text.text.toString()

                if (phone_number.trim().isEmpty()) {
                    login_form_feedback.text = "Пожалуйста введите номер телефона"
                    login_form_feedback.visibility = View.VISIBLE
                } else if (phone_number.trim().length != 10) {
                    login_form_feedback.text =
                        "Пожалуйста введите корректный номер xxx-xxx-xx-xx"
                    login_form_feedback.visibility = View.VISIBLE
                } else {

                    viewModel.getUser("+$complete_phone_number", this)
                    viewModel.response.observe(this, Observer {

                        if ("+${complete_phone_number}" == it.driver_profile.phones[0]) {

                            sharedPreferences.edit()
                                .putString("USER_PHONE_NUMBER", "+${complete_phone_number}")
                                .putBoolean("USER_REGISTERED", true)
                                .apply()
                            viewModel.getMessageStatus(this, "+$complete_phone_number")
                            viewModel.responseOtp.observe(
                                this,
                                Observer {
                                    val intent = Intent(this, MobizonActivity::class.java)
                                    intent.putExtra("phoneNumber", "+$complete_phone_number")
                                    intent.putExtra("verCode", it)
                                    startActivity(intent)
                                    finish()
                                })
                        } else {
                            Log.d(
                                "Check",
                                "+${complete_phone_number}" + " " + it.driver_profile.phones[0]
                            )
                            generate_btn.isEnabled = true
                            login_form_feedback.visibility = View.VISIBLE
                            login_form_feedback.text =
                                " Данный номер не зарегистрирован в нашей базе. Попробуйте еще раз."
                        }
                    })
                }
            }
        }
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