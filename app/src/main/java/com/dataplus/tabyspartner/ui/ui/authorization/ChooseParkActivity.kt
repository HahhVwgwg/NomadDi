package com.dataplus.tabyspartner.ui.ui.authorization

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dataplus.tabyspartner.MainActivity
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.adapter.ChooseParkAdapter
import com.dataplus.tabyspartner.databinding.ActivityChooseParkBinding
import com.dataplus.tabyspartner.model.ResultResponse
import com.dataplus.tabyspartner.networking.ParkElement
import com.dataplus.tabyspartner.ui.ui.pin.VerificationActivity
import com.dataplus.tabyspartner.utils.SharedHelper
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*


class ChooseParkActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChooseParkBinding
    private var phone: String? = null
    private var otp: String? = null

    private val viewModel: AuthorizationViewModel by lazy {
        ViewModelProvider(this, ViewModelFactory.getInstance()).get(AuthorizationViewModel::class.java)
    }

    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkConnectivity()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_choose_park)
        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        binding.lifecycleOwner = this
        binding.parkElements.layoutManager = LinearLayoutManager(
            this
        )
        if (callingActivity?.className == "com.dataplus.tabyspartner.ui.ui.authorization.MobizonActivity") {
            phone = intent.getStringExtra("phoneNumber").toString()
            otp = intent.getStringExtra("otp").toString()
        } else {
            return
        }

        phone = intent.getStringExtra("phoneNumber").toString()
        otp = intent.getStringExtra("otp").toString()
        parkElementRequest()
        viewModel.responseParkElement.observe(this, {
            when (it) {
                is ResultResponse.Loading -> {
                }
                is ResultResponse.Success -> {
                    if (it.data.isNotEmpty())
                        initAdapter(it.data as MutableList<ParkElement>)
                }
                is ResultResponse.Error -> {
                    Toast.makeText(this, "Выберите парк", Toast.LENGTH_SHORT).show()
                }
            }
        })

        viewModel.responseOtp.observe(this, {
            redirectToPinCodeActivity(it)
        })
        binding.generateBtn.setOnClickListener {
            requestLogin()
        }
        binding.root
    }

    private fun requestLogin() {
        try {
            registerByToken((binding.parkElements.adapter as ChooseParkAdapter).getParkPositionFleet())
        } catch (e: AssertionError) {
            Toast.makeText(this, R.string.choose_park_element, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initAdapter(mutableList: MutableList<ParkElement>) {
        binding.parkElements.adapter = ChooseParkAdapter(mutableList)
    }

    private fun registerByToken(parkPositionFleet: Long) {
        if (SharedHelper.getKey(this, "device_token").isEmpty()) {
            getToken()
            return
        }
        val map = HashMap<String, Any>()
        map["device_token"] = SharedHelper.getKey(this, "device_token", "No device")
        map["device_id"] = SharedHelper.getKey(this, "device_id", "123")
        map["device_type"] = "android"
        map["otp"] = otp!!
        map["mobile"] = phone!!
        map["fleet_id"] = parkPositionFleet
        map["nomad"] = true
        viewModel.loginByOtp(map)
    }

    private fun parkElementRequest() {
        if (SharedHelper.getKey(this, "device_token").isEmpty()) {
            getToken()
            return
        }
        val map = HashMap<String, Any>()
        map["device_token"] = SharedHelper.getKey(this, "device_token", "No device")
        map["device_id"] = SharedHelper.getKey(this, "device_id", "123")
        map["device_type"] = "android"
        map["otp"] = otp!!
        map["mobile"] = phone!!
        viewModel.getParkElements(map)
    }

    private fun redirectToPinCodeActivity(accessToken: String) {
        SharedHelper.putKey(this, "access_token", accessToken)
        sharedPreferences.edit()
            .putBoolean("USERPHONE_REGISTERED", true)
            .putBoolean("USER_PIN_CODE_CREATED", false).apply()
        startActivity(Intent(this, VerificationActivity()::class.java))
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
            requestLogin()
        })
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

}