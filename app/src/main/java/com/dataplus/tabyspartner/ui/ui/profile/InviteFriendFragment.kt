package com.dataplus.tabyspartner.ui.ui.profile

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.telephony.SmsManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dataplus.tabyspartner.MainActivity
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.databinding.FragmentInviteBinding
import com.redmadrobot.inputmask.MaskedTextChangedListener


class InviteFriendFragment : Fragment() {

    private lateinit var binding: FragmentInviteBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invite, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
        sharedPreferences = inflater.context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setToolbarTitle(getString(R.string.invite_friend_title), true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val listener = MaskedTextChangedListener("+7 ([000]) [000]-[00]-[00]", binding.inputPhone,
            object : MaskedTextChangedListener.ValueListener {
                override fun onTextChanged(
                    maskFilled: Boolean,
                    extractedValue: String,
                    formattedValue: String
                ) {

                }
            })
        binding.inputPhone.addTextChangedListener(listener)
        binding.inputPhone.onFocusChangeListener = listener
        binding.sendInviteBtn.setOnClickListener {
            val ref = binding.inputPhone.text.toString()
                .replace("+", "")
                .replace(" ", "")
                .replace("(", "")
                .replace(")", "")
                .replace("-", "")
            if (ref.length < 10) {
                Toast.makeText(it.context, "Укажите номер телефона", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val imm =
                it.context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.inputPhone.windowToken, 0)
            sharedPreferences.getString("USER_PHONE_NUMBER", "")?.let { userPhoneNumber ->
                attemptSendSms(userPhoneNumber.replace("+", ""), ref)
            }
        }
        viewModel.responseInvite.observe(viewLifecycleOwner, {
            if (it != null) {
                viewModel.responseInvite.postValue(null)
                Toast.makeText(view.context, it, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun attemptSendSms(phone: String, ref: String) {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.SEND_SMS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.SEND_SMS),
                REQUEST_SMS
            )
        } else {
            sendSms(phone, ref)
        }
    }

    private fun sendSms(phone: String, ref: String) {
        try {
            val smsMgrVar: SmsManager = SmsManager.getDefault()
            smsMgrVar.sendTextMessage(ref, null, "Рекомендую \"Tabys Go\"\n" +
                    "https://play.google.com/store/apps/details?id=com.dataplus.tabyspartner", null, null)
            viewModel.sendInvite(phone, ref)
        } catch (e: Exception) {
            Toast.makeText(requireContext(), e.toString(), Toast.LENGTH_SHORT).show()
        }

    }

    companion object {
        private const val REQUEST_SMS = 2
    }
}