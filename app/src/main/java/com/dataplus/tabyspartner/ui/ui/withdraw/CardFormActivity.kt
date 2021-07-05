package com.dataplus.tabyspartner.ui.ui.withdraw

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.dataplus.tabyspartner.MainActivity
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.databinding.ActivityCardFormBinding
import com.dataplus.tabyspartner.ui.ui.authorization.Authorization
import com.dataplus.tabyspartner.utils.FourDigitCardFormatWatcher
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class CardFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardFormBinding
    private lateinit var viewModel: CardViewModel


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_card_form)
        checkConnectivity()
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
        setSupportActionBar(toolbar)
        viewModel = ViewModelProvider(this).get(CardViewModel::class.java)
        supportActionBar?.setTitle("")
        toolbarTitle.setText("Добавление карты")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        viewModel.error.observe(this, {
            Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
            if (it == "token_invalid"){
                val sharedPreferences =
                    applicationContext.getSharedPreferences("app_prefs", MODE_PRIVATE)!!
                sharedPreferences.edit().clear().apply()
                applicationContext.startActivity(
                    Intent(
                        applicationContext,
                        Authorization::class.java
                    )
                )
                finish()
            }
        })
        binding.root
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
        binding.cardNumberEditText.addTextChangedListener(FourDigitCardFormatWatcher());
        binding.cardNumberRepeatEditText.addTextChangedListener(FourDigitCardFormatWatcher());
        binding.generateBtn.setOnClickListener {
            var number = ""
            var numberRepeat = ""
            for (i in binding.cardNumberEditText.text.toString()) {
                if (i == ' ') continue
                number += i
            }
            for (i in binding.cardNumberRepeatEditText.text.toString()) {
                if (i == ' ') continue
                numberRepeat += i
            }
            //Log.d("CheckNUmber",number)
            if (binding.cardNameEditText.text.toString().trim().isEmpty() ||
                binding.cardNumberEditText.text.toString().trim().isEmpty() ||
                binding.cardNumberRepeatEditText.text.toString().trim().isEmpty()
            ) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.error_empty))
                    .setPositiveButton(resources.getString(R.string.accept2)) { dialog, which ->
                        // Respond to positive button press finishAffinity()
                        dialog.dismiss()
                    }
                    .show()
            } else if (number.length < 16 || numberRepeat.length < 16) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.error_length))
                    .setPositiveButton(resources.getString(R.string.accept2)) { dialog, which ->
                        // Respond to positive button press finishAffinity()
                        dialog.dismiss()
                    }
                    .show()
            } else if (!number.trim().isDigitsOnly() || !numberRepeat.trim().isDigitsOnly()) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.errorAcceptanceCreditCard))
                    .setPositiveButton(resources.getString(R.string.accept2)) { dialog, which ->
                        // Respond to positive button press finishAffinity()
                        dialog.dismiss()
                    }
                    .show()
            } else {
                if (number == numberRepeat) {
//                    val db = DatabaseHandler(this)
//                    if (db.insertTask(
//                            CreditCard(
//                                1,
//                                binding.cardNameEditText.text.toString().trim(),
//                                number.trim()
//                            )
//                        )
//                    ) {
//                        setResult(Activity.RESULT_OK)
//                        onBackPressed()
//                    }

                    val hashMap = HashMap<String, Any>()
                    hashMap["card_id"] = number.trim()
                    hashMap["brand"] = if (number[0] == '4') "V" else "M"
                    hashMap["card_name"] = binding.cardNameEditText.text.toString().trim()

                    viewModel.addCard(hashMap)
                    viewModel.response.observe(this, {
                        Toast.makeText(this, it, Toast.LENGTH_LONG).show()
                        setResult(Activity.RESULT_OK)
                        onBackPressed()
                    })
                } else {
                    MaterialAlertDialogBuilder(this)
                        .setTitle(resources.getString(R.string.notSameError))
                        .setPositiveButton(resources.getString(R.string.accept2)) { dialog, which ->
                            // Respond to positive button press finishAffinity()
                            dialog.dismiss()
                        }
                        .show()
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