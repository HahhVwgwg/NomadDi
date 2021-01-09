package com.example.tabyspartner.ui.ui.withdraw

import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import com.example.tabyspartner.MainActivity
import com.example.tabyspartner.R
import com.example.tabyspartner.databinding.ActivityCardFormBinding
import com.example.tabyspartner.model.CreditCard
import com.example.tabyspartner.utils.DatabaseHandler
import com.example.tabyspartner.utils.FourDigitCardFormatWatcher
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class CardFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardFormBinding


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_card_form)
        checkConnectivity()
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("")
        toolbarTitle.setText("Добавление карты")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.root
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
        binding.cardNumberEditText.addTextChangedListener(FourDigitCardFormatWatcher());
        binding.generateBtn.setOnClickListener {
            var number = ""
            for (i in binding.cardNumberEditText.text.toString()) {
                if (i == ' ') continue
                number += i
            }
            //Log.d("CheckNUmber",number)
            if (binding.cardNameEditText.text.toString().trim().isEmpty() ||
                binding.cardNumberEditText.text.toString().trim().isEmpty()
            ) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.error_empty))
                    .setPositiveButton(resources.getString(R.string.accept2)) { dialog, which ->
                        // Respond to positive button press finishAffinity()
                        dialog.dismiss()
                    }
                    .show()
            } else if (number.length < 16) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.error_length))
                    .setPositiveButton(resources.getString(R.string.accept2)) { dialog, which ->
                        // Respond to positive button press finishAffinity()
                        dialog.dismiss()
                    }
                    .show()
            } else if (!number.trim().isDigitsOnly()) {
                MaterialAlertDialogBuilder(this)
                    .setTitle(resources.getString(R.string.errorAcceptanceCreditCard))
                    .setPositiveButton(resources.getString(R.string.accept2)) { dialog, which ->
                        // Respond to positive button press finishAffinity()
                        dialog.dismiss()
                    }
                    .show()
            } else {
                val db = DatabaseHandler(this)
                db.insertTask(
                    CreditCard(
                        1,
                        binding.cardNameEditText.text.toString().trim(),
                        number.trim()
                    )
                )
                setResult(Activity.RESULT_OK)
                onBackPressed()
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