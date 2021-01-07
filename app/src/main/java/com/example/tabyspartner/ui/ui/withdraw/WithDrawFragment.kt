package com.example.tabyspartner.ui.ui.withdraw

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.tabyspartner.R
import com.example.tabyspartner.databinding.FragmentMainPageBinding
import com.example.tabyspartner.databinding.FragmentWithDrawBinding
import com.example.tabyspartner.modal.ModalBottomSheet
import kotlinx.android.synthetic.main.fragment_with_draw.*


class WithDrawFragment : Fragment() {
    private lateinit var binding: FragmentWithDrawBinding
    private lateinit var viewModel : WithDrawViewModel
    private lateinit var bindingMainPageBinding: FragmentMainPageBinding
    lateinit var sharedPreferences : SharedPreferences
    private lateinit var model: ModalBottomSheet.SharedViewModel
    val modalbottomSheetFragment = ModalBottomSheet()
    @RequiresApi(Build.VERSION_CODES.P)
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
            binding.balanceAmountWithDrawPage.text =
                it.accounts[0].balance.toDouble().toInt().toString() + " \u20b8"
        })

        model = activity?.run {
            ViewModelProviders.of(this).get(ModalBottomSheet.SharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        model.selected.postValue("Выберите карту");
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
        model.selected.observe(viewLifecycleOwner, Observer<String> { item ->
            binding.chooseCardBtn.setText(item)
        })

        binding.withDrawAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(binding.withDrawAmount.text.toString() == "") {
                    binding.amountFee.text =
                        "Комиссия 150 ₸"
                    binding.withdrawBtnWithdrawPage.text =
                        "Перевести 0 \u20b8"
                }else if(binding.withDrawAmount.text.toString().toInt() < 251) {
                    binding.amountFee.text =
                        "Комиссия 150 ₸"
                    binding.withdrawBtnWithdrawPage.text =
                        "Перевод невозможен \u20b8"
                } else if(binding.withDrawAmount.text.toString().length > 4) {
                    val calculateFee = (binding.withDrawAmount.text.toString().toDouble() * (0.015)).toInt().toString()
                    binding.amountFee.text =
                        "Комиссия ${calculateFee} ₸"
                    binding.withdrawBtnWithdrawPage.text =
                        "Перевести ${binding.withDrawAmount.text.toString().toInt() - calculateFee.toInt()} \u20b8"
                } else if(binding.withDrawAmount.text.toString().toInt() > 251) {
                    val calculateFee = (binding.withDrawAmount.text.toString().toDouble() * (0.015)).toInt().toString()
                    binding.amountFee.text =
                        "Комиссия 150 ₸"
                    binding.withdrawBtnWithdrawPage.text =
                        "Перевести ${binding.withDrawAmount.text.toString().toInt() - 150} \u20b8"
                } else {
                    binding.amountFee.text =
                        "Комиссия 150 ₸"
                    binding.withdrawBtnWithdrawPage.text =
                        "Перевести ${binding.withDrawAmount.text.toString().toInt() - 150} \u20b8"
                }
            }
            override fun afterTextChanged(p0: Editable?) {
            }
        });
        binding.chooseCardBtn.setOnClickListener {
            modalbottomSheetFragment.show(requireFragmentManager(), modalbottomSheetFragment.tag)
        }
        binding.withdrawBtnWithdrawPage.setOnClickListener {
            if(binding.withDrawAmount.text.toString() == "") {
                val dialogBuilder = AlertDialog.Builder(this.requireContext())
                dialogBuilder.setMessage("Невозможно вывести 0 \u20b8")
                    .setCancelable(false)
                    .setPositiveButton("Повторить", DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
                val alert = dialogBuilder.create()
                alert.setTitle("Ошибка")
                alert.show()
            }else if(binding.withDrawAmount.text.toString().toInt() < 251) {
                val dialogBuilder = AlertDialog.Builder(this.requireContext())
                dialogBuilder.setMessage("Для вывода средств баланс должен быть не менее 251 \u20b8")
                    .setCancelable(false)
                    .setPositiveButton("Повторить", DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
                val alert = dialogBuilder.create()
                alert.setTitle("Вывод средств невозможен")
                alert.show()
            } else if(!binding.chooseCardBtn.text.toString().isDigitsOnly()) {
                val dialogBuilder = AlertDialog.Builder(this.requireContext())
                dialogBuilder.setMessage("Пожалуйста, выберите вашу карту")
                    .setCancelable(false)
                    .setPositiveButton("Повторить", DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
                val alert = dialogBuilder.create()
                alert.setTitle("Вы не выбрали карту перевода!")
                alert.show()
            }else {
                viewModel.withdrawCash(binding.withDrawAmount.text.toString(),binding.chooseCardBtn.text.toString(),this.requireContext(),this)
               binding.withdrawBtnWithdrawPage.isEnabled = false
            }
        }
    }
}