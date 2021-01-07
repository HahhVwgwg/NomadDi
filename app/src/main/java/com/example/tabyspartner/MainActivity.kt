package com.example.tabyspartner

import android.Manifest
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.tabyspartner.databinding.ActivityMainBinding
import com.example.tabyspartner.ui.ui.main.MainPageFragment
import com.example.tabyspartner.ui.ui.profile.ProfileFragment
import com.example.tabyspartner.ui.ui.withdraw.WithDrawFragment


class MainActivity() : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var sharedPreferences: SharedPreferences
    private val REQUEST_CALL = 1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        val toolbarTitle = findViewById<TextView>(R.id.toolbar_title)
        setSupportActionBar(toolbar)
        supportActionBar?.setTitle("")
        toolbarTitle.setText("Главная")
        checkConnectivity()
        handleFrame(MainPageFragment())
        binding.bottomNavigation.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.support_menu_item -> {
                    makePhoneCall()
                    true
                }
                R.id.main_menu_item -> {
                    toolbarTitle.setText("Главная")
                    handleFrame(MainPageFragment())
                    true
                }
                R.id.withdraw_menu_item -> {
                    toolbarTitle.setText("Вывод средств")
                    handleFrame(WithDrawFragment())
                    true
                }
                R.id.profile_menu_item -> {
                    toolbarTitle.setText("Профиль")
                    handleFrame(ProfileFragment())
                    true
                }
                else -> false
            }
        }
    }

    override fun onStart() {
        super.onStart()
    }

    private fun makePhoneCall() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CALL_PHONE),
                REQUEST_CALL
            );
        } else {
            startCall()
        }
    }

    private fun startCall() {
        val callIntent = Intent(Intent.ACTION_DIAL)
        callIntent.data = Uri.parse("tel:" + getString(R.string.phone_number))
        startActivity(callIntent)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CALL) {
            startCall()
        }
    }

    private fun handleFrame(fragment: Fragment): Boolean {
        val fragmentTransaction = this.supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.navHostFragment, fragment).commit()
        return true
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