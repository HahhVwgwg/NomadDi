package com.example.tabyspartner.ui.ui.history

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.tabyspartner.R

class HistoryActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("")
        toolbarTitle.setText("История транзакции")
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            finish()
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
    }
}