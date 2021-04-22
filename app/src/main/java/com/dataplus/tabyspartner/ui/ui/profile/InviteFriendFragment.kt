package com.dataplus.tabyspartner.ui.ui.profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.databinding.FragmentInviteBinding

class InviteFriendFragment : Fragment() {

    private lateinit var binding: FragmentInviteBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_invite, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
        sharedPreferences = inflater.context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.inputPhone.addTextChangedListener {

        }
        binding.sendInviteBtn.setOnClickListener {
            val ref = binding.inputPhone.text.toString()
            if (ref.length < 10) {
                Toast.makeText(it.context, "Укажите номер телефона", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val imm = it.context.getSystemService(AppCompatActivity.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.inputPhone.windowToken, 0)
            sharedPreferences.getString("USER_PHONE_NUMBER", "")?.let { userPhoneNumber ->
                viewModel.sendInvite(userPhoneNumber.replace("+", ""), ref)
            }
        }
        viewModel.responseInvite.observe(viewLifecycleOwner, {
            if (it != null) {
                viewModel.responseInvite.postValue(null)
                Toast.makeText(view.context, it, Toast.LENGTH_SHORT).show()
            }
        })
    }
}