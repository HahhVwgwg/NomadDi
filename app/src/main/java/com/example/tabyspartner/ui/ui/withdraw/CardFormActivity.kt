package com.example.tabyspartner.ui.ui.withdraw

import android.app.Activity
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import com.example.tabyspartner.R
import com.example.tabyspartner.databinding.ActivityCardFormBinding
import com.example.tabyspartner.model.CreditCard
import com.example.tabyspartner.utils.DatabaseHandler




class CardFormActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardFormBinding


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_card_form)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("")
        toolbarTitle.setText("Добавление карты")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.generateBtn.setOnClickListener {
            val db = DatabaseHandler(this)
            db.insertTask(CreditCard(1,binding.cardNameEditText.text.toString().trim(),binding.cardNumberEditText.text.toString().trim()))
            setResult(Activity.RESULT_OK)
        }
        binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
    }


}