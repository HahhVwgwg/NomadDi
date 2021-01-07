package com.example.tabyspartner.ui.ui.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.tabyspartner.R
import com.example.tabyspartner.databinding.FragmentProfileBinding
import com.example.tabyspartner.ui.ui.pin.VerificationActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile,container,false)

        sharedPreferences = context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)!!

        binding.profileFragName.text = sharedPreferences.getString("USER_SHORT_NAME","")
        binding.profileFragMobile.text = sharedPreferences.getString("USER_PHONE_NUMBER","")
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

        binding.logOutBtn.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.logOut_title))
                .setMessage(resources.getString(R.string.logOut_message))
                .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                    // Respond to negative button press
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                    // Respond to positive button press
                    requireActivity().finishAffinity()
                }
                .show()
        }
    }

}