package com.example.tabyspartner.ui.ui.authorization

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
import com.example.tabyspartner.MainActivity
import com.example.tabyspartner.R
import com.example.tabyspartner.databinding.ActivityMobizonBinding
import com.example.tabyspartner.ui.ui.main.MainPageFragment
import com.example.tabyspartner.ui.ui.pin.VerificationActivity

class MobizonActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMobizonBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_mobizon)
        checkConnectivity()
        binding.root
    }

    override fun onResume() {
        super.onResume()
        val otp: String? = intent.getStringExtra("verCode")
        //val phone: String? = intent.getStringExtra("phoneNumber")
        binding.verifyBtn.setOnClickListener {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.mainLayoutMobizon.getWindowToken(), 0)
            binding.otpProgressBar.visibility = View.VISIBLE
            if(binding.otpTextView.text.toString() == otp) {
                startActivity(Intent(this, VerificationActivity()::class.java))
                finish()
            }else {
                binding.otpFormFeedback.text = "Код неверный убедитесь что вы ввели правильный код"
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
            dialogBuilder.setMessage("Make sure that WI-FI or mobile data is turned on, then try again")
                // if the dialog is cancelable
                .setCancelable(false)
                // positive button text and action
                .setPositiveButton("Retry", DialogInterface.OnClickListener { dialog, id ->
                    recreate()
                })
                // negative button text and action
                .setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, id ->
                    finish()
                })

            // create dialog box
            val alert = dialogBuilder.create()
            // set title for alert dialog box
            alert.setTitle("No Internet Connection")
            alert.setIcon(R.mipmap.ic_launcher)
            // show alert dialog
            alert.show()
        }
    }
}