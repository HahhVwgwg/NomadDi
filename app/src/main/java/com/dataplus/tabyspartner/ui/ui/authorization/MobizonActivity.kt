package com.dataplus.tabyspartner.ui.ui.authorization

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dataplus.tabyspartner.MainActivity
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.databinding.ActivityMobizonBinding
import com.dataplus.tabyspartner.ui.ui.pin.VerificationActivity


class MobizonActivity : AppCompatActivity() {


    private var timer: CountDownTimer? = null
    private lateinit var binding: ActivityMobizonBinding
    private val viewModel: AuthorizationViewModel by lazy {
        ViewModelProvider(this).get(AuthorizationViewModel::class.java)
    }
    private lateinit var sharedPreferences: SharedPreferences
    var isRegisteredPhone = false
    var isPinCodeCreated = false
    private lateinit var otp: String
    //error code
    //make button send code again
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mobizon)
        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        binding.lifecycleOwner = this
        isRegisteredPhone = sharedPreferences.getBoolean("USERPHONE_REGISTERED", false)
        isPinCodeCreated = sharedPreferences.getBoolean("USER_PIN_CODE_CREATED", false)
        //isRegisteredMobizonCode = sharedPreferences.getBoolean("USERMOBIZONCODE_REGISTERED", false)
        checkConnectivity()
        binding.root
        resetTimer()
        otp = intent.getStringExtra("verCode").toString()
    }

    @SuppressLint("CommitPrefEdits")
    override fun onResume() {
        super.onResume()


        binding.sendCodeAgain.setOnClickListener {
            if (binding.sendCodeAgain.text == "Отправить повторно") {
                binding.verifyBtn.isEnabled = false
                binding.otpProgressBar.isEnabled = true
                binding.otpProgressBar.visibility = View.VISIBLE
                viewModel.getMessageStatus(this, "+${intent.getStringExtra("phoneNumber")}")
                viewModel.responseOtp.observe(
                    binding.lifecycleOwner as MobizonActivity,
                    Observer {
                        otp = it
                        binding.verifyBtn.isEnabled = true
                        binding.otpProgressBar.isEnabled = false
                        binding.otpProgressBar.visibility = View.INVISIBLE
                    })
                resetTimer()
            }
        }


        //val phone: String? = intent.getStringExtra("phoneNumber")
        binding.verifyBtn.setOnClickListener {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.mainLayoutMobizon.getWindowToken(), 0)
            binding.otpProgressBar.visibility = View.VISIBLE
            if (binding.otpTextView.text.toString() == otp) {
                sharedPreferences.edit()
                    .putBoolean("USERPHONE_RfEGISTERED", true)
                    .putBoolean("USER_PIN_CODE_CREATED", false).apply()
                startActivity(Intent(this, VerificationActivity()::class.java))
                finish()
            } else {
                binding.otpFormFeedback.text = "Код неверный убедитесь что вы ввели правильный код"
            }
        }
    }

    private fun resetTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(5000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.sendCodeAgain.text =
                    "Отправить повторно через " + millisUntilFinished / 1000 + " сек"
                binding.sendCodeAgain.background = null
            }

            override fun onFinish() {
                binding.sendCodeAgain.text = "Отправить повторно"
                binding.sendCodeAgain.background = ContextCompat.getDrawable(this@MobizonActivity, R.drawable.card_chooser_btn_bbg)
            }
        }.start()
    }

    private fun checkConnectivity() {
        val manager = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = manager.activeNetworkInfo

        if (null == activeNetwork) {
            val dialogBuilder = AlertDialog.Builder(this)
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
}