package com.example.tabyspartner.ui.ui.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.tabyspartner.R
import com.example.tabyspartner.databinding.FragmentProfileBinding
import com.example.tabyspartner.ui.ui.authorization.Authorization
import com.example.tabyspartner.ui.ui.pin.VerificationActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    lateinit var sharedPreferences: SharedPreferences
    var pinCode = ""
    var isPinCodeCreated = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile,container,false)

        sharedPreferences = context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)!!
        isPinCodeCreated = sharedPreferences.getBoolean("USER_PIN_CODE_CREATED",false)
        pinCode = sharedPreferences.getString("USER_PIN_CODE","")!!

        return binding.root
    }


    override fun onResume() {
        super.onResume()
        binding.changePinCodeBtn.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.change_pincode_title))
                .setMessage(resources.getString(R.string.change_pincode_message))
                .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                    // Respond to negative button press
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                    // Respond to positive button press
                    sharedPreferences.edit().remove("USER_PIN_CODE_CREATED").apply()
                    sharedPreferences.edit().remove("USER_PIN_CODE").apply()
                    context?.startActivity(Intent(requireContext(), VerificationActivity::class.java))
                }
                .show()


        }
    }

}