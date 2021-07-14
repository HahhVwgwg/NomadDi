package com.dataplus.tabyspartner.ui.ui.withdraw

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
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
import com.dataplus.tabyspartner.networking.WalletTransation
import com.dataplus.tabyspartner.ui.ui.authorization.Authorization
import com.dataplus.tabyspartner.ui.ui.pin.VerificationActivity
import com.dataplus.tabyspartner.ui.ui.withdraw.adapter.HistoryAdapter

class HistoryFragment : Fragment() {

    companion object {
        private const val MODE = "extra.mode"
        fun getInstance(mode: Int) = HistoryFragment().apply {
            arguments = Bundle().apply {
                putInt(MODE, mode)
            }
        }
    }
    private fun appClickListener(position: Int) {
        val intent = Intent(activity, HistoryActivity::class.java)
        intent.putExtra("id", position)
        startActivity(intent)
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
        viewModel.error.observe(viewLifecycleOwner, {
            val dialogBuilder = AlertDialog.Builder(this.requireContext())
                dialogBuilder.setMessage(it)
                    .setCancelable(false)
                    .setPositiveButton(
                        "Повторить"
                    ) { dialog, _ ->
                        dialog.dismiss()
                    }
                val alert = dialogBuilder.create()
                alert.show()
            if (it == "token_invalid"){
                sharedPreferences.edit().clear().apply()
                requireContext().startActivity(
                    Intent(
                        requireContext(),
                        Authorization::class.java
                    )
                )
                requireActivity().finish()
            }
        })
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.empty.text = "Выводов еще не было"
        val mode = arguments?.getInt(MODE, 0) ?: 0
        viewModel.getWalletTransaction()
        (if (mode == 0) viewModel.responseHistory else viewModel.responseHistoryRef).observe(viewLifecycleOwner, {
            when (it) {
                is ResultResponse.Loading -> {
                }
                is ResultResponse.Success -> {
                    binding.list.adapter = HistoryAdapter(it.data as List<WalletTransation>,clickListener = { it1 -> appClickListener(it1)}, mode)
                    binding.empty.visibility = if (it.data.isNullOrEmpty()) View.VISIBLE else View.GONE
                }
                is ResultResponse.Error -> {
                    Toast.makeText(view.context, it.message, Toast.LENGTH_SHORT).show()
                }
            }

        })
    }

    override fun onResume() {
        super.onResume()
        val mode = arguments?.getInt(MODE, 0) ?: 0
        (activity as? MainActivity)?.setToolbarTitle(
            getString(if (mode == 0) R.string.history_title else R.string.history_title_ref),
            true
        )
//        sharedPreferences.getString("USER_PHONE_NUMBER", "")?.let { userPhoneNumber ->
//            viewModel.getHistory(userPhoneNumber.replace("+", ""), mode)
//        }
    }
}