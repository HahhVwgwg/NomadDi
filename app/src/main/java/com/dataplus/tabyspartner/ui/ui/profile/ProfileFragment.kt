package com.dataplus.tabyspartner.ui.ui.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.dataplus.tabyspartner.BuildConfig
import com.dataplus.tabyspartner.MainActivity
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.databinding.FragmentProfileBinding
import com.dataplus.tabyspartner.ui.ui.authorization.Authorization
import com.dataplus.tabyspartner.ui.ui.pin.VerificationActivity
import com.dataplus.tabyspartner.utils.DatabaseHandler
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProfileFragment : Fragment() {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var myDb: DatabaseHandler

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)


        binding.appVersion.text = getString(
            R.string.profile_app_version,
            "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        )
        sharedPreferences = context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)!!
        myDb = DatabaseHandler(requireContext())
        binding.profileFragName.text = sharedPreferences.getString("USER_SHORT_NAME", "")
        binding.profileFragMobile.text = sharedPreferences.getString("USER_PHONE_NUMBER", "")
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setToolbarTitle("Профиль", false)
        binding.changePinCodeBtn.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.change_pincode_title))
                .setMessage(resources.getString(R.string.change_pincode_message))
                .setNegativeButton(resources.getString(R.string.decline)) { dialog, _ ->
                    // Respond to negative button press
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                    // Respond to positive button press
                    context?.startActivity(
                        Intent(
                            requireContext(),
                            VerificationActivity::class.java
                        ).putExtra("USER_FROM_PROFILE", true)
                    )
                }
                .show()
        }
        binding.inviteFriendBtn.setOnClickListener {
            handleFrame(InviteFriendFragment())
        }
        binding.goIncomesBtn.setOnClickListener {
            handleFrame(IncomesFragment())
        }
        binding.goNotificationsBtn.setOnClickListener {
            handleFrame(NotificationsFragment())
        }
        binding.logOutBtn.setOnClickListener {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.logOut_title))
                .setMessage(resources.getString(R.string.logOut_message))
                .setNegativeButton(resources.getString(R.string.decline)) { dialog, _ ->
                    // Respond to negative button press
                    dialog.dismiss()
                }
                .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                    // Respond to positive button press
                    sharedPreferences.edit().clear().apply()
                    myDb.deleteAllData()
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

    private fun handleFrame(fragment: Fragment) {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.addToBackStack(fragment::class.java.simpleName)
        fragmentTransaction.replace(R.id.navHostFragment, fragment, fragment::class.java.simpleName)
            .commit()
    }

}