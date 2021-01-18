package com.dataplus.tabyspartner.ui.ui.withdraw

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.databinding.FragmentMainPageBinding
import com.dataplus.tabyspartner.databinding.FragmentWithDrawBinding
import com.dataplus.tabyspartner.modal.ModalBottomSheet
import com.dataplus.tabyspartner.ui.ui.pin.VerificationActivity2


class WithDrawFragment : Fragment() {
    private lateinit var binding: FragmentWithDrawBinding
    private lateinit var viewModel: WithDrawViewModel
    private lateinit var bindingMainPageBinding: FragmentMainPageBinding
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var model: ModalBottomSheet.SharedViewModel
    val modalbottomSheetFragment = ModalBottomSheet()
    private lateinit var driver_id: String

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

        // Log.i("MainPageFragment", "Called ViewModelProviders.of!")
        viewModel = ViewModelProviders.of(this).get(WithDrawViewModel::class.java)
        sharedPreferences = context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)!!
        val userPhoneNumber = sharedPreferences.getString("USER_PHONE_NUMBER", "")
        viewModel.getYandexDriversProperties(userPhoneNumber!!)
        viewModel.responseD.observe(viewLifecycleOwner, Observer {
            binding.balanceAmountWithDrawPage.text =
                it.accounts[0].balance.toDouble().toInt().toString() + " \u20b8"
            driver_id = it.driver_profile.id
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

        viewModel.getYandexDriversProperties(sharedPreferences.getString("USER_PHONE_NUMBER", "")!!)
        model.selected.observe(viewLifecycleOwner, Observer<String> { item ->
            binding.chooseCardBtn.setText(item)
        })
        binding.withDrawAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.withDrawAmount.text.toString() == "" || binding.withDrawAmount.text.toString()
                        .toInt() < 150
                ) {
                    binding.amountFee.text =
                        "Комиссия 150 ₸"
                    binding.withdrawBtnWithdrawPage.text =
                        "Перевести 0 \u20b8"
                } else if (binding.withDrawAmount.text.toString().length > 4) {
                    val calculateFee =
                        (binding.withDrawAmount.text.toString().toDouble() * (0.015)).toInt()
                            .toString()
                    binding.amountFee.text =
                        "Комиссия ${calculateFee} ₸"
                    binding.withdrawBtnWithdrawPage.text =
                        "Перевести ${
                            binding.withDrawAmount.text.toString().toInt() - calculateFee.toInt()
                        } \u20b8"
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
            val withDrawAmount = binding.balanceAmountWithDrawPage.text.substring(
                0,
                binding.balanceAmountWithDrawPage.text.length - 2
            )
            if (!binding.chooseCardBtn.text.toString().isDigitsOnly()) {
                val dialogBuilder = AlertDialog.Builder(this.requireContext())
                dialogBuilder.setMessage("Пожалуйста, выберите вашу карту")
                    .setCancelable(false)
                    .setPositiveButton("Повторить", DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
                val alert = dialogBuilder.create()
                alert.setTitle("Вы не выбрали карту перевода!")
                alert.show()
            } else if (binding.withDrawAmount.text.toString() == "") {
                val dialogBuilder = AlertDialog.Builder(this.requireContext())
                dialogBuilder.setMessage("Минимальная сумма перевода 200 \u20b8")
                    .setCancelable(false)
                    .setPositiveButton(
                        "Повторить",
                        DialogInterface.OnClickListener { dialog, id ->
                            dialog.dismiss()
                        })
                val alert = dialogBuilder.create()
                alert.setTitle("Вывод средств невозможен")
                alert.show()
            } else if (binding.withDrawAmount.text.toString()
                    .toInt() <= 199
            ) {
                if (withDrawAmount.toInt() - binding.withDrawAmount.text.toString().toInt() < 100) {
                    val dialogBuilder = AlertDialog.Builder(this.requireContext())
                    dialogBuilder.setMessage("На вашем балансе должно оставаться не менее 100 тг, чтобы вы могли получать \"наличные\" заказы.")
                        .setCancelable(false)
                        .setPositiveButton(
                            "Повторить",
                            DialogInterface.OnClickListener { dialog, id ->
                                dialog.dismiss()
                            })
                    val alert = dialogBuilder.create()
                    alert.setTitle("Вывод средств невозможен")
                    alert.show()
                } else {
                    val dialogBuilder = AlertDialog.Builder(this.requireContext())
                    dialogBuilder.setMessage("Минимальная сумма перевода 200 \u20b8")
                        .setCancelable(false)
                        .setPositiveButton(
                            "Повторить",
                            DialogInterface.OnClickListener { dialog, id ->
                                dialog.dismiss()
                            })
                    val alert = dialogBuilder.create()
                    alert.setTitle("Вывод средств невозможен")
                    alert.show()
                }
            } else if (binding.withDrawAmount.text.toString().toInt() == withDrawAmount.toInt()) {
                val dialogBuilder = AlertDialog.Builder(this.requireContext())
                dialogBuilder.setMessage("На вашем балансе должно оставаться не менее 100 тг, чтобы вы могли получать \"наличные\" заказы.")
                    .setCancelable(false)
                    .setPositiveButton("Повторить", DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
                val alert = dialogBuilder.create()
                alert.setTitle("Вывод средств невозможен")
                alert.show()
            } else if (binding.withDrawAmount.text.toString().toInt() > withDrawAmount.toInt()) {
                val dialogBuilder = AlertDialog.Builder(this.requireContext())
                dialogBuilder.setMessage("Не достаточно средств")
                    .setCancelable(false)
                    .setPositiveButton("Повторить", DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
                val alert = dialogBuilder.create()
                alert.setTitle("Вывод средств невозможен")
                alert.show()
            } else if (binding.withDrawAmount.text.toString().toInt() > 199) {
                if (withDrawAmount.toString().toInt() - binding.withDrawAmount.text.toString()
                        .toInt() < 100
                ) {
                    val dialogBuilder = AlertDialog.Builder(this.requireContext())
                    dialogBuilder.setMessage("На вашем балансе должно оставаться не менее 100 тг, чтобы вы могли получать \"наличные\" заказы.")
                        .setCancelable(false)
                        .setPositiveButton(
                            "Повторить",
                            DialogInterface.OnClickListener { dialog, id ->
                                dialog.dismiss()
                            })
                    val alert = dialogBuilder.create()
                    alert.setTitle("Вывод средств невозможен")
                    alert.show()
                } else {
                    startActivityForResult(
                        Intent(requireContext(), VerificationActivity2::class.java),
                        1
                    )
                    binding.withdrawBtnWithdrawPage.isEnabled = false
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            viewModel.withDrawCashFromYandexViewModelFun(
                amount = "-${binding.withDrawAmount.text.toString()}",
                driverId = driver_id
            )
            viewModel.responseWithDrawYandex.observe(viewLifecycleOwner, Observer {
                if (driver_id == it.driver_profile_id) {
                    viewModel.withdrawCash(
                        binding.withDrawAmount.text.toString(),
                        binding.chooseCardBtn.text.toString(),
                        this.requireContext(),
                        this
                    )
                    val dialogBuilder = AlertDialog.Builder(this.requireContext())
                    dialogBuilder.setMessage("Операция прошла успешно. Ждите пополнения.")
                        .setCancelable(false)
                        .setPositiveButton(
                            "Ок",
                            DialogInterface.OnClickListener { dialog, id ->
                                binding.withdrawBtnWithdrawPage.isEnabled = true
                                dialog.dismiss()
                            })
                    val alert = dialogBuilder.create()
                    alert.show()
                } else {
                    Toast.makeText(requireContext(), "Операция провалена", Toast.LENGTH_SHORT)
                        .show()
                }
            })
        }
    }
}