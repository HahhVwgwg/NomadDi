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
import android.util.Log
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_with_draw.*


class WithDrawFragment : Fragment() {
    //private lateinit var binding: FragmentWithDrawBinding
    private lateinit var viewModel: WithDrawViewModel

    //private lateinit var bindingMainPageBinding: FragmentMainPageBinding
    lateinit var sharedPreferences: SharedPreferences
    private lateinit var model: ModalBottomSheet.SharedViewModel
    val modalbottomSheetFragment = ModalBottomSheet()

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //Log.i("MainPageFragment", "Called ViewModelProviders.of!")
        viewModel = ViewModelProviders.of(this).get(WithDrawViewModel::class.java)
        sharedPreferences = context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)!!
        val userPhoneNumber = sharedPreferences.getString("USER_PHONE_NUMBER", "")
        viewModel.getYandexDriversProperties(userPhoneNumber!!)
        viewModel.responseD.observe(viewLifecycleOwner, Observer {
            balance_amount_with_draw_page.text =
                it.accounts[0].balance.toDouble().toInt().toString() + " \u20b8"
        })

        model = activity?.run {
            ViewModelProviders.of(this).get(ModalBottomSheet.SharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        model.selected.postValue("Выберите карту");
        return inflater.inflate(R.layout.fragment_with_draw, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()

        viewModel.getYandexDriversProperties(sharedPreferences.getString("USER_PHONE_NUMBER", "")!!)
        model.selected.observe(viewLifecycleOwner, Observer<String> { item ->
            choose_card_btn.setText(item)
        })
        with_draw_amount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (with_draw_amount.text.toString() == "" || with_draw_amount.text.toString()
                        .toInt() < 130
                ) {
                    amountFee.text =
                        "Комиссия 130 ₸"
                    withdraw_btn_withdrawPage.text =
                        "Перевести 0 \u20b8"
                } else if (with_draw_amount.text.toString().length > 4) {
                    val calculateFee =
                        (with_draw_amount.text.toString().toDouble() * (0.013)).toInt()
                            .toString()
                    amountFee.text =
                        "Комиссия ${calculateFee} ₸"
                    withdraw_btn_withdrawPage.text =
                        "Перевести ${
                            with_draw_amount.text.toString().toInt() - calculateFee.toInt()
                        } \u20b8"
                } else {
                    amountFee.text =
                        "Комиссия 130 ₸"
                    withdraw_btn_withdrawPage.text =
                        "Перевести ${with_draw_amount.text.toString().toInt() - 130} \u20b8"
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        });

        choose_card_btn.setOnClickListener {
            modalbottomSheetFragment.show(requireFragmentManager(), modalbottomSheetFragment.tag)
        }

        withdraw_btn_withdrawPage.setOnClickListener {
            val withDrawAmount = balance_amount_with_draw_page.text.substring(
                0,
                balance_amount_with_draw_page.text.length - 2
            )
            if (!choose_card_btn.text.toString().isDigitsOnly()) {
                val dialogBuilder = AlertDialog.Builder(this.requireContext())
                dialogBuilder.setMessage("Пожалуйста, выберите вашу карту")
                    .setCancelable(false)
                    .setPositiveButton("Повторить", DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
                val alert = dialogBuilder.create()
                alert.setTitle("Вы не выбрали карту перевода!")
                alert.show()
            } else if (with_draw_amount.text.toString() == "") {
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
            } else if (with_draw_amount.text.toString()
                    .toInt() <= 199
            ) {
                if (withDrawAmount.toInt() - with_draw_amount.text.toString().toInt() < 100) {
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
            } else if (with_draw_amount.text.toString().toInt() == withDrawAmount.toInt()) {
                val dialogBuilder = AlertDialog.Builder(this.requireContext())
                dialogBuilder.setMessage("На вашем балансе должно оставаться не менее 100 тг, чтобы вы могли получать \"наличные\" заказы.")
                    .setCancelable(false)
                    .setPositiveButton("Повторить", DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
                val alert = dialogBuilder.create()
                alert.setTitle("Вывод средств невозможен")
                alert.show()
            } else if (with_draw_amount.text.toString().toInt() > withDrawAmount.toInt()) {
                val dialogBuilder = AlertDialog.Builder(this.requireContext())
                dialogBuilder.setMessage("Не достаточно средств")
                    .setCancelable(false)
                    .setPositiveButton("Повторить", DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
                val alert = dialogBuilder.create()
                alert.setTitle("Вывод средств невозможен")
                alert.show()
            } else if (with_draw_amount.text.toString().toInt() > 199) {
                if (withDrawAmount.toString().toInt() - with_draw_amount.text.toString()
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
                    withdraw_btn_withdrawPage.isEnabled = false
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            viewModel.withDrawCashFromYandexViewModelFun(
                amount = with_draw_amount.text.toString(),
                choose_card_btn.text.toString(),
                context
            )
            viewModel.responseWithDrawYandex.observe(viewLifecycleOwner, Observer {
                if (it == true) {
                    viewModel.consumeResult()
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(resources.getString(R.string.operation_ok))
                        .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                            withdraw_btn_withdrawPage.isEnabled = true
                            dialog.dismiss()
                        }
                        .show()
                }
            })
        }
    }
}