package com.dataplus.tabyspartner.ui.ui.pin

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.dataplus.tabyspartner.MainActivity
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.ui.ui.authorization.Authorization
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_verification2.*

class VerificationActivity2 : AppCompatActivity() {
    lateinit var sharedPreferences: SharedPreferences
    var pinCode = ""
    var isPinCodeCreated = false
    //private lateinit var binding: com.dataplus.tabyspartner.databinding.ActivityVerification2Binding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkConnectivity()
        setContentView(R.layout.activity_verification2)

        sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        isPinCodeCreated = sharedPreferences.getBoolean("USER_PIN_CODE_CREATED", false)
        pinCode = sharedPreferences.getString("USER_PIN_CODE", "")!!
        tv_input_tip.text = "Введите ваш код доступа для подтверждения перевода"
        textField2.visibility = View.GONE
        textField.hint = "Введите код"
        forgetPassword.visibility = View.VISIBLE
    }

    override fun onResume() {
        super.onResume()
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
                setResult(Activity.RESULT_OK)
                finish()
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