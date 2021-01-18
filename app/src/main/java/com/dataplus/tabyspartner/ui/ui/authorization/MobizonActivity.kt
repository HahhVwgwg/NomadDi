package com.dataplus.tabyspartner.ui.ui.authorization

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.dataplus.tabyspartner.MainActivity
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.databinding.ActivityMobizonBinding
import com.dataplus.tabyspartner.ui.ui.pin.VerificationActivity
import kotlinx.android.synthetic.main.activity_mobizon.*

class MobizonActivity : AppCompatActivity() {
    //private lateinit var binding: ActivityMobizonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mobizon)
        checkConnectivity()
        //binding.root
    }

    override fun onResume() {
        super.onResume()
        val otp: String? = intent.getStringExtra("verCode")
        //val phone: String? = intent.getStringExtra("phoneNumber")
       verify_btn.setOnClickListener {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(mainLayoutMobizon.getWindowToken(), 0)
            otp_progress_bar.visibility = View.VISIBLE
            if(otp_text_view.text.toString() == otp) {
                startActivity(Intent(this, VerificationActivity()::class.java))
                finish()
            }else {
                otp_form_feedback.text = "Код неверный убедитесь что вы ввели правильный код"
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