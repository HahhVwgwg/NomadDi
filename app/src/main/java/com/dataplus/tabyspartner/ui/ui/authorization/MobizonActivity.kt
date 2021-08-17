package com.dataplus.tabyspartner.ui.ui.authorization

import android.annotation.SuppressLint
import android.content.*
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
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
import com.dataplus.tabyspartner.utils.SharedHelper
import com.dataplus.tabyspartner.utils.SmsBroadcastReceiver
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*
import java.util.regex.Pattern


class MobizonActivity : AppCompatActivity() {


    private lateinit var phone: String
    private var timer: CountDownTimer? = null
    private lateinit var binding: ActivityMobizonBinding
    private val viewModel: AuthorizationViewModel by lazy {
        ViewModelProvider(this, ViewModelFactory.getInstance()).get(AuthorizationViewModel::class.java)
    }
    private lateinit var sharedPreferences: SharedPreferences
    var isRegisteredPhone = false
    var isPinCodeCreated = false
    private lateinit var otp: String
    private val REQ_USER_CONSENT = 200
    var smsBroadcastReceiver: SmsBroadcastReceiver? = null

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
        println("wow" + callingActivity?.className)
        if (callingActivity?.className == "com.dataplus.tabyspartner.ui.ui.authorization.Authorization") {
            otp = intent.getStringExtra("verCode").toString()
            phone = intent.getStringExtra("phoneNumber").toString()
        } else {
            return
        }

        startSmartUserConsent()

    }

    fun startSmartUserConsent() {
        val client = SmsRetriever.getClient(applicationContext)
        client.startSmsUserConsent(null)
    }


    @SuppressLint("CommitPrefEdits")
    override fun onResume() {
        super.onResume()


        binding.sendCodeAgain.setOnClickListener {
            if (binding.sendCodeAgain.text == "Отправить повторно") {
                binding.verifyBtn.isEnabled = false
                binding.otpProgressBar.isEnabled = true
                binding.otpProgressBar.visibility = View.VISIBLE
                viewModel.getUser(intent.getStringExtra("phoneNumber")!!)
                viewModel.response.observe(
                    binding.lifecycleOwner as MobizonActivity,
                    Observer {
                        otp = it.otp!!
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
                registerByToken()
            } else {
                binding.otpFormFeedback.text = "Код неверный убедитесь что вы ввели правильный код"
            }
        }
    }

    private fun registerBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver!!.smsBroadcastReceiverListener =
            object : SmsBroadcastReceiver.SmsBroadcastReceiverListener {
                override fun onSuccess(intent: Intent?) {
                    startActivityForResult(
                        intent, REQ_USER_CONSENT
                    )
                }

                override fun onFailure() {}
            }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsBroadcastReceiver, intentFilter)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_USER_CONSENT) {
            if (resultCode == RESULT_OK && data != null) {
                val message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                getOtpFromMessage(message!!)
            }
        }
    }

    private fun getOtpFromMessage(message: String) {
        val otpPattern = Pattern.compile("(|^)\\d{6}")
        val matcher = otpPattern.matcher(message)
        if (matcher.find()) {
            binding.otpTextView.setText(matcher.group(0))
        }
    }

    override fun onStart() {
        super.onStart()
        registerBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsBroadcastReceiver)
    }


    private fun registerByToken() {
        if (SharedHelper.getKey(this, "device_token").isEmpty()) {
            getToken()
            return
        }
        val map = HashMap<String, Any>()
        map["device_token"] = SharedHelper.getKey(this, "device_token", "No device")
        map["device_id"] = SharedHelper.getKey(this, "device_id", "123")
        map["device_type"] = "android"
        map["otp"] = otp
        map["mobile"] = phone
        map["nomad"] = true
        viewModel.loginByOtp(map)
        viewModel.responseOtp.observe(this, {
            redirectToPinCodeActivity(it)
        })
        viewModel.error.observe(this, {
            redirectToChooseParkActivity()
        })
    }

    private fun redirectToPinCodeActivity(accessToken: String) {
        SharedHelper.putKey(this, "access_token", accessToken)
        sharedPreferences.edit()
            .putBoolean("USERPHONE_REGISTERED", true)
            .putBoolean("USER_PIN_CODE_CREATED", false).apply()
        startActivity(Intent(this, VerificationActivity()::class.java))
        finish()
    }

    private fun redirectToChooseParkActivity() {
        val intent = Intent(this, ChooseParkActivity()::class.java)
        intent.putExtra("phoneNumber", phone)
        intent.putExtra("otp", otp)
        startActivityForResult(intent, 300)
        finish()
    }

    private fun getToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("device_token", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            // Log and toast
            Log.d("device_token", token!!)
            SharedHelper.putKey(this, "device_token", token)
            registerByToken()
        })
    }

    private fun resetTimer() {
        timer?.cancel()
        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                binding.sendCodeAgain.text =
                    "Отправить повторно через " + millisUntilFinished / 1000 + " сек"
                binding.sendCodeAgain.background = null
            }

            override fun onFinish() {
                binding.sendCodeAgain.text = "Отправить повторно"
                binding.sendCodeAgain.background =
                    ContextCompat.getDrawable(this@MobizonActivity, R.drawable.card_chooser_btn_bbg)
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
                .setPositiveButton(R.string.retry) { _, _ ->
                    recreate()
                }
                // negative button text and action
                .setNegativeButton(R.string.otmenit) { _, _ ->
                    finish()
                }

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle(R.string.internet_title)
            // show alert dialog
            alert.show()
        }
    }


}