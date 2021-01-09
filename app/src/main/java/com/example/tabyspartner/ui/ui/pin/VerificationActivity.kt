package com.example.tabyspartner.ui.ui.pin

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.tabyspartner.MainActivity
import com.example.tabyspartner.R
import com.example.tabyspartner.ui.ui.authorization.Authorization
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.util.*


class VerificationActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    var pinCode = ""
    var isPinCodeCreated = false
    private lateinit var binding: com.example.tabyspartner.databinding.ActivityVerificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        checkConnectivity()
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_verification)
        binding.lifecycleOwner = this

        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        isPinCodeCreated = sharedPreferences.getBoolean("USER_PIN_CODE_CREATED", false)
        pinCode = sharedPreferences.getString("USER_PIN_CODE", "")!!

        binding.root
    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onResume() {
        super.onResume()
        if (isPinCodeCreated) {
            binding.tvInputTip.text = "Введите ваш код доступа"
            binding.textField2.visibility = View.GONE
            binding.textField.hint = "Введите код"
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
                        startActivity(Intent(this, Authorization::class.java))
                        finish()
                    }
                    .show()
            }
            binding.verifyPinCodeBtn.setOnClickListener {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.mainLayoutVer.getWindowToken(), 0)
                if (binding.pinCodeText.text!!.trim().isEmpty()) {
                    binding.verifyCodeFeedBack.text = "Поле не должно быть пустым"
                    binding.verifyCodeFeedBack.visibility = View.VISIBLE
                } else if (binding.pinCodeText.text!!.trim().toString() != pinCode) {
                    binding.verifyCodeFeedBack.text = "Неверный пинкод. Попробуйте еще раз."
                    binding.verifyCodeFeedBack.visibility = View.VISIBLE
                } else if (binding.pinCodeText.text!!.trim().toString() == pinCode) {
                    binding.verifyPinCodeBtn.isEnabled = false
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        } else {
            binding.verifyPinCodeBtn.setOnClickListener {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.mainLayoutVer.getWindowToken(), 0)
                val firstInput = binding.pinCodeText.text!!.trim()
                val secondInput = binding.pinCodeVerifyText.text!!.trim()
                if (binding.pinCodeText.text!!.trim()
                        .isEmpty() || binding.pinCodeVerifyText.text!!.trim().isEmpty()
                ) {
                    binding.verifyCodeFeedBack.text = "Поле не должно быть пустым"
                    binding.verifyCodeFeedBack.visibility = View.VISIBLE
                } else if (binding.pinCodeText.text!!.length != 4) {
                    binding.verifyCodeFeedBack.text = "Код должен состоять из 4 цифр"
                    binding.verifyCodeFeedBack.visibility = View.VISIBLE
                } else if (binding.pinCodeText.text!!.trim() != binding.pinCodeVerifyText.text!!.trim()) {
                    binding.verifyCodeFeedBack.text = "Код не совпадает"
                    binding.verifyCodeFeedBack.visibility = View.VISIBLE
                } else {
                    binding.verifyCodeFeedBack.visibility = View.INVISIBLE
                    binding.verifyPinCodeBtn.isEnabled = false
                    sharedPreferences.edit()
                        .putString("USER_PIN_CODE", binding.pinCodeText.text!!.trim().toString())
                        .putBoolean("USER_PIN_CODE_CREATED", true)
                        .apply()
                    Toast.makeText(this@VerificationActivity, "Пинкод создан", Toast.LENGTH_SHORT)
                        .show()
                    startActivity(Intent(this@VerificationActivity, MainActivity::class.java))
                    finish()
                }

            }
        }

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