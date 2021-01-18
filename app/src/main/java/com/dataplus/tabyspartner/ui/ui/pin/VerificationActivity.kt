package com.dataplus.tabyspartner.ui.ui.pin

import android.annotation.SuppressLint
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dataplus.tabyspartner.MainActivity
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.ui.ui.authorization.Authorization
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_verification.*
import java.util.*


class VerificationActivity : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    var pinCode = ""
    var isPinCodeCreated = false
    var fromProfile = false
    //private lateinit var binding: com.dataplus.tabyspartner.databinding.ActivityVerificationBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        checkConnectivity()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        isPinCodeCreated = sharedPreferences.getBoolean("USER_PIN_CODE_CREATED", false)
        pinCode = sharedPreferences.getString("USER_PIN_CODE", "")!!
        fromProfile = sharedPreferences.getBoolean("USER_FROM_PROFILE", false)

    }


    @SuppressLint("ClickableViewAccessibility")
    override fun onResume() {
        super.onResume()

        if (fromProfile) {
            var firstInput = ""
            var secondInput = ""
            pin_code_text.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int
                ) {
                }
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    firstInput = p0.toString()
                    //Log.d("textCheck",firstInput)
                }
                override fun afterTextChanged(p0: Editable?) {
                }
            })
            pin_code_verify_text.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    p0: CharSequence?,
                    p1: Int,
                    p2: Int,
                    p3: Int
                ) {
                }
                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                    secondInput = p0.toString()
                    //Log.d("text2Check",secondInput)
                }
                override fun afterTextChanged(p0: Editable?) {
                }
            })
            textField3.visibility = View.VISIBLE
            verify_pin_code_btn.setOnClickListener {
                val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(mainLayoutVer.getWindowToken(), 0)
                if(current_pin_code_text.text.toString() == "") {
                    verify_codeFeedBack.text = "Введите текущий пинкод"
                    verify_codeFeedBack.visibility = View.VISIBLE
                }else if(current_pin_code_text.text.toString() == pinCode) {
                    if (firstInput.trim().isEmpty() || secondInput.trim().isEmpty()
                    ) {
                        verify_codeFeedBack.text = "Поле не должно быть пустым"
                        verify_codeFeedBack.visibility = View.VISIBLE
                    } else if (firstInput.length != 4) {
                        verify_codeFeedBack.text = "Код должен состоять из 4 цифр"
                        verify_codeFeedBack.visibility = View.VISIBLE
                    } else if (firstInput.trim() != secondInput.trim()) {
                        verify_codeFeedBack.text = "Новый пин код не совпадает"
                        verify_codeFeedBack.visibility = View.VISIBLE
                    } else {
                        verify_codeFeedBack.visibility = View.INVISIBLE
                        verify_codeFeedBack.isEnabled = false
                        sharedPreferences.edit().remove("USER_PIN_CODE_CREATED").apply()
                        sharedPreferences.edit().remove("USER_PIN_CODE").apply()
                        sharedPreferences.edit()
                            .putString(
                                "USER_PIN_CODE",
                                pin_code_text.text!!.trim().toString()
                            )
                            .putBoolean("USER_PIN_CODE_CREATED", true)
                            .putBoolean("USER_FROM_PROFILE",false)
                            .apply()
                        Toast.makeText(
                            this@VerificationActivity,
                            "Пинкод создан",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        startActivity(Intent(this@VerificationActivity, MainActivity::class.java))
                        finish()
                    }
                }
                else {
                    verify_codeFeedBack.text = "Введенный текущий пинкод не совпадает"
                    verify_codeFeedBack.visibility = View.VISIBLE
                }
            }
            fromProfile = false
            ////////////////////////////////
        } else {
            if (isPinCodeCreated) {
                tv_input_tip.text = "Введите ваш код доступа"
                textField2.visibility = View.GONE
                textField.hint = "Введите код"
                forgetPassword.visibility = View.VISIBLE

                forgetPassword.setOnClickListener {
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
                verify_pin_code_btn.setOnClickListener {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(mainLayoutVer.getWindowToken(), 0)
                    if (pin_code_text.text!!.trim().isEmpty()) {
                        verify_codeFeedBack.text = "Поле не должно быть пустым"
                        verify_codeFeedBack.visibility = View.VISIBLE
                    } else if (pin_code_text.text!!.trim().toString() != pinCode) {
                        verify_codeFeedBack.text = "Неверный пинкод. Попробуйте еще раз."
                        verify_codeFeedBack.visibility = View.VISIBLE
                    } else if (pin_code_text.text!!.trim().toString() == pinCode) {
                        verify_pin_code_btn.isEnabled = false
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    }
                }
            } else {
                var firstInput = ""
                var secondInput = ""
                pin_code_text.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        p0: CharSequence?,
                        p1: Int,
                        p2: Int,
                        p3: Int
                    ) {
                    }
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        firstInput = p0.toString()
                        //Log.d("textCheck",firstInput)
                    }
                    override fun afterTextChanged(p0: Editable?) {
                    }
                })
                pin_code_verify_text.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(
                        p0: CharSequence?,
                        p1: Int,
                        p2: Int,
                        p3: Int
                    ) {
                    }
                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                        secondInput = p0.toString()
                        //Log.d("text2Check",secondInput)
                    }
                    override fun afterTextChanged(p0: Editable?) {
                    }
                })
                verify_pin_code_btn.setOnClickListener {
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(mainLayoutVer.getWindowToken(), 0)

                    if (firstInput.trim()
                            .isEmpty() || secondInput.trim().isEmpty()
                    ) {
                        verify_codeFeedBack.text = "Поле не должно быть пустым"
                        verify_codeFeedBack.visibility = View.VISIBLE
                    } else if (firstInput.length != 4) {
                        verify_codeFeedBack.text = "Код должен состоять из 4 цифр"
                        verify_codeFeedBack.visibility = View.VISIBLE
                    } else if (firstInput.trim() != secondInput.trim()) {
                        verify_codeFeedBack.text = "Код не совпадает"
                        verify_codeFeedBack.visibility = View.VISIBLE
                    } else {
                        verify_codeFeedBack.visibility = View.INVISIBLE
                        verify_pin_code_btn.isEnabled = false
                        sharedPreferences.edit()
                            .putString(
                                "USER_PIN_CODE",
                                pin_code_text.text!!.trim().toString()
                            )
                            .putBoolean("USER_PIN_CODE_CREATED", true)
                            .apply()
                        Toast.makeText(
                            this@VerificationActivity,
                            "Пинкод создан",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                        startActivity(Intent(this@VerificationActivity, MainActivity::class.java))
                        finish()
                    }

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