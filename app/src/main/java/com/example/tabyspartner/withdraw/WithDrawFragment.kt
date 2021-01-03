package com.example.tabyspartner.withdraw

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.tabyspartner.R
import com.example.tabyspartner.databinding.FragmentMainPageBinding
import com.example.tabyspartner.databinding.FragmentWithDrawBinding
import com.example.tabyspartner.main.MainPageViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog


class WithDrawFragment : Fragment() {

    private lateinit var binding: FragmentWithDrawBinding
    private lateinit var viewModel : WithDrawViewModel
    private lateinit var bindingMainPageBinding: FragmentMainPageBinding
    lateinit var sharedPreferences : SharedPreferences
    private val viewModelMainPageViewModel: MainPageViewModel by lazy {
        ViewModelProvider(this).get(MainPageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_with_draw, container, false
        )
        bindingMainPageBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_main_page, container, false
        )
        Log.i("MainPageFragment", "Called ViewModelProviders.of!")
        viewModel = ViewModelProviders.of(this).get(WithDrawViewModel::class.java)
        sharedPreferences = context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)!!
        val userPhoneNumber = sharedPreferences.getString("USER_PHONE_NUMBER", "")


        viewModel.getYandexDriversProperties(userPhoneNumber!!)
        viewModel.responseD.observe(viewLifecycleOwner, Observer {
            //Log.d("responseDriver", it.toString())
            binding.balanceAmountWithDrawPage.text =
                it.accounts[0].balance.toDouble().toInt().toString() + "\u20b8"
        })

        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.withDrawAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                binding.withdrawBtnWithdrawPage.text =
                    "Перевести ${binding.withDrawAmount.text} \u20b8"

            }

            override fun afterTextChanged(p0: Editable?) {

            }

        });
        binding.chooseCardBtn.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(
                requireContext(),
                R.style.BottomSheetDialogTheme
            )
            val bottomSheetView = LayoutInflater.from(requireContext())
                .inflate(
                    R.layout.bottom_sheet_dialog_card_picker,
                    requireActivity().findViewById(R.id.bottomSheetContainer)
                )
            var cardNumber = ""
            bottomSheetView.findViewById<Button>(R.id.button_dialog_sheet).setOnClickListener{
                //binding.chooseCardBtn.text = "**** **** **** ${cardNumber.substring(cardNumber.length-4)}"
                cardNumber =  bottomSheetView.findViewById<EditText>(R.id.card_number_text).text.toString()
                binding.chooseCardBtn.text = cardNumber
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }


        binding.withdrawBtnWithdrawPage.setOnClickListener {

           // Log.d("BukhtaCheckValid",binding.withDrawAmount.text.toString()+" "+binding.chooseCardBtn.text.toString())
            //viewModel.withdrawCash(binding.withDrawAmount.text.toString(),binding.chooseCardBtn.text.toString())
        }
    }
}