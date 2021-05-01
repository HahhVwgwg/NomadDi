package com.dataplus.tabyspartner.ui.ui.withdraw

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
import com.dataplus.tabyspartner.MainActivity
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.databinding.FragmentIncomesBinding
import com.dataplus.tabyspartner.model.ResultResponse
import com.dataplus.tabyspartner.ui.ui.profile.adapter.IncomesAdapter

class HistoryFragment : Fragment() {

    companion object {
        private const val MODE = "extra.mode"
        fun getInstance(mode: Int) = HistoryFragment().apply {
            arguments = Bundle().apply {
                putInt(MODE, mode)
            }
        }
    }

    private lateinit var binding: FragmentIncomesBinding
    private lateinit var viewModel: WithDrawViewModel
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_incomes, container, false)
        sharedPreferences = inflater.context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        viewModel = ViewModelProvider(requireActivity()).get(WithDrawViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        /*viewModel.responseIncomes.observe(viewLifecycleOwner, {
            when (it) {
                is ResultResponse.Loading -> {
                }
                is ResultResponse.Success -> {
                    binding.list.adapter = IncomesAdapter(it.data)
                }
                is ResultResponse.Error -> {
                    Toast.makeText(view.context, it.message, Toast.LENGTH_SHORT).show()
                }
            }

        })*/
    }

    override fun onResume() {
        super.onResume()
        val mode = arguments?.getInt(MODE, 0) ?: 0
        (activity as? MainActivity)?.setToolbarTitle(
            getString(if (mode == 0) R.string.history_title else R.string.history_title_ref),
            true
        )
        sharedPreferences.getString("USER_PHONE_NUMBER", "")?.let { userPhoneNumber ->
            viewModel.getHistory(userPhoneNumber.replace("+", ""), mode)
        }
    }
}