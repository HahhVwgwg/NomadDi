package com.dataplus.tabyspartner.ui.ui.profile

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.databinding.FragmentIncomesBinding
import com.dataplus.tabyspartner.model.ResultResponse
import com.dataplus.tabyspartner.ui.ui.profile.adapter.IncomesAdapter

class IncomesFragment : Fragment() {

    private lateinit var binding: FragmentIncomesBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_incomes, container, false)
        sharedPreferences = inflater.context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        viewModel = ViewModelProvider(requireActivity()).get(ProfileViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.responseIncomes.observe(viewLifecycleOwner, {
            viewModel.responseIncomes.observe(viewLifecycleOwner, {
                when (it) {
                    is ResultResponse.Loading -> {}
                    is ResultResponse.Success -> {
                        binding.list.adapter = IncomesAdapter(it.data)
                    }
                    is ResultResponse.Error -> {
                        Toast.makeText(view.context, it.message, Toast.LENGTH_SHORT).show()
                    }
                }

            })
        })
        binding.back.setOnClickListener {
            activity?.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        sharedPreferences.getString("USER_PHONE_NUMBER", "")?.let { userPhoneNumber ->
            viewModel.getIncomes(userPhoneNumber.replace("+", ""))
        }
    }
}