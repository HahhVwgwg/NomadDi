package com.dataplus.tabyspartner.ui.ui.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.dataplus.tabyspartner.BuildConfig
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.databinding.FragmentProfileBinding
import com.dataplus.tabyspartner.ui.ui.authorization.Authorization
import com.dataplus.tabyspartner.ui.ui.pin.VerificationActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment : Fragment() {
    //private lateinit var binding: FragmentProfileBinding
    lateinit var sharedPreferences: SharedPreferences
    var fromProfile = false;
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        sharedPreferences = context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)!!
        profile_frag_name.text = sharedPreferences.getString("USER_SHORT_NAME", "")
        profile_frag_mobile.text = sharedPreferences.getString("USER_PHONE_NUMBER", "")
        fromProfile = sharedPreferences.getBoolean("USER_FROM_PROFILE",false)
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        app_version.text = getString(R.string.profile_app_version, "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
    }

    override fun onResume() {
        super.onResume()
        change_pin_code_btn.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.change_pincode_title))
                .setMessage(resources.getString(R.string.change_pincode_message))
                .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                    // Respond to negative button press
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                    // Respond to positive button press
                    sharedPreferences.edit().putBoolean("USER_FROM_PROFILE",true).apply()
//                    sharedPreferences.edit().remove("USER_PIN_CODE").apply()
                    context?.startActivity(
                        Intent(
                            requireContext(),
                            VerificationActivity::class.java
                        )
                    )
                }
                .show()
        }

       log_out_btn.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.logOut_title))
                .setMessage(resources.getString(R.string.logOut_message))
                .setNegativeButton(resources.getString(R.string.decline)) { dialog, which ->
                    // Respond to negative button press
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                    // Respond to positive button press
                    sharedPreferences.edit().clear().apply()
                    context?.startActivity(
                        Intent(
                            requireContext(),
                            Authorization::class.java
                        )
                    )
                    requireActivity().finish()
                }
                .show()
        }
    }

}