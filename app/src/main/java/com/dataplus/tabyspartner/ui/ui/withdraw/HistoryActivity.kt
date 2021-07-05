package com.dataplus.tabyspartner.ui.ui.withdraw

import android.annotation.SuppressLint
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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.dataplus.tabyspartner.MainActivity
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.databinding.ActivityCardFormBinding
import com.dataplus.tabyspartner.databinding.ActivityHistoryBinding
import com.dataplus.tabyspartner.ui.ui.authorization.Authorization
import kotlinx.android.synthetic.main.activity_history.view.*
import java.text.SimpleDateFormat
import java.util.*


class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private lateinit var viewModel: HistoryViewModel


    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_history)
        checkConnectivity()
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
        setSupportActionBar(toolbar)
        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        supportActionBar?.title = ""
        toolbarTitle.text = "Детали транзакции"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        viewModel.getHistoryById(intent.getIntExtra("id",0))
        binding.root
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
        viewModel.response.observe(this, {
            if (it != null){
                binding.amount.text = it.walletDetails.amount.toString() + " ₸"
                binding.createdAt.text = SimpleDateFormat(
                    "dd/MM/yyyy",
                    Locale.getDefault()
                ).format(
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(
                        it.walletDetails.createdAt
                    ) ?: Date()
                )
                binding.exactTime.text = SimpleDateFormat(
                    "HH:mm",
                    Locale.getDefault()
                ).format(
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(
                        it.walletDetails.createdAt
                    ) ?: Date()
                )
                when (it.walletDetails.status) {
                    "pending" -> {
                        binding.status.text = "В ожидании"
                        binding.status.setBackgroundResource(R.drawable.bg_pending)
                    }
                    "processed" -> {
                        binding.status.text = "Завершен"
                        binding.status.setBackgroundResource(R.drawable.bg_success)
                    }
                    "cancelled" -> {
                        binding.status.text = "Отменен"
                        binding.status.setBackgroundResource(R.drawable.bg_cancelled)
                    }
                    else -> {
                        binding.status.text = "Ошибка"
                        binding.status.setBackgroundResource(R.drawable.bg_failed)
                    }
                }
                binding.transactionDesc.text = it.walletDetails.transactionDesc
                binding.cardName.text = it.walletDetails.cardName
                binding.cardNumber.text = it.walletDetails.cardNumber
                binding.transactionAlias.text = it.walletDetails.transactionAlias
                binding.openBalance.text = it.walletDetails.openBalance.toString()
                binding.closeBalance.text = it.walletDetails.closeBalance.toString()
            } else {
                Toast.makeText(applicationContext, "Ошибка", Toast.LENGTH_SHORT).show()
            }
        })
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