package com.dataplus.tabyspartner.ui.ui.withdraw

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dataplus.tabyspartner.MainActivity
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.databinding.FragmentMainPageBinding
import com.dataplus.tabyspartner.databinding.FragmentWithDrawBinding
import com.dataplus.tabyspartner.modal.ModalBottomSheet
import com.dataplus.tabyspartner.ui.ui.pin.VerificationActivity2
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_with_draw.*

class WithDrawFragment : Fragment() {

    companion object {
        private const val TAG = "WithDrawFragment"
    }

    private lateinit var binding: FragmentWithDrawBinding
    private lateinit var viewModel: WithDrawViewModel
    private lateinit var bindingMainPageBinding: FragmentMainPageBinding
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var model: ModalBottomSheet.SharedViewModel
    val modalBottomSheetFragment = ModalBottomSheet()

    private var timer: CountDownTimer? = null

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_with_draw, container, false
        )
        bindingMainPageBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_main_page, container, false
        )

        viewModel = ViewModelProvider(requireActivity()).get(WithDrawViewModel::class.java)
        sharedPreferences = context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)!!

        model = activity?.run {
            ViewModelProvider(this).get(ModalBottomSheet.SharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        model.selected.postValue("Выберите карту")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        count(
            60000L - (System.currentTimeMillis() - sharedPreferences.getLong(
                "USER_WITHDRAW_TIME",
                0L
            ))
        ) {
            sharedPreferences.getString("USER_PHONE_NUMBER", "")?.let { userPhoneNumber ->
                viewModel.getDriversProperties(userPhoneNumber.replace("+", ""))
            }
        }
        viewModel.moneySource.observe(viewLifecycleOwner, {
            val balance = viewModel.balance.value
            when (it) {
                0 -> {
                    binding.myBalanceTitle.text = getString(R.string.balance_menu)
                    binding.balanceAmountWithDrawPage.text = String.format("%s %s",
                        balance?.first ?: "0", "\u20b8")
                }
                1 -> {
                    binding.myBalanceTitle.text = getString(R.string.partners_menu)
                    binding.balanceAmountWithDrawPage.text = String.format("%s %s",
                        balance?.second ?: "0", "\u20b8")
                }
            }
        })
        viewModel.balance.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            when (viewModel.moneySource.value) {
                0 -> {
                    binding.balanceAmountWithDrawPage.text = String.format("%s %s",
                        it.first, "\u20b8")
                }
                1 -> {
                    binding.balanceAmountWithDrawPage.text = String.format("%s %s",
                        it.second, "\u20b8")
                }
            }
        })
        viewModel.error.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            viewModel.consumeError()
        })
        binding.myBalanceHistory.setOnClickListener {
            handleFrame(HistoryFragment.getInstance(viewModel.moneySource.value ?: 0))
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
        (activity as? MainActivity)?.setToolbarTitle("Вывод средств", false)
        model.selected.observe(viewLifecycleOwner, { item ->
            binding.chooseCardBtn.text = item
        })
        binding.withDrawAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            @SuppressLint("SetTextI18n")
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (binding.withDrawAmount.text.toString() == "" || binding.withDrawAmount.text.toString()
                        .toInt() < 130
                ) {
                    binding.amountFee.text =
                        "Комиссия 130 ₸"
                    binding.withdrawBtnWithdrawPage.text =
                        "Перевести 0 \u20b8"
                } else if (binding.withDrawAmount.text.toString().length > 4) {
                    val calculateFee =
                        (binding.withDrawAmount.text.toString().toDouble() * (0.013)).toInt()
                            .toString()
                    binding.amountFee.text =
                        "Комиссия $calculateFee ₸"
                    binding.withdrawBtnWithdrawPage.text =
                        "Перевести ${
                            binding.withDrawAmount.text.toString().toInt() - calculateFee.toInt()
                        } \u20b8"
                } else {
                    binding.amountFee.text =
                        "Комиссия 130 ₸"
                    binding.withdrawBtnWithdrawPage.text =
                        "Перевести ${binding.withDrawAmount.text.toString().toInt() - 130} \u20b8"
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.chooseCardBtn.setOnClickListener {
            modalBottomSheetFragment.show(parentFragmentManager, modalBottomSheetFragment.tag)
        }
        binding.myBalanceTitle.setOnClickListener {
            val popupMenu = PopupMenu(it.context, it)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { mi ->
                when (mi.itemId) {
                    R.id.balance -> viewModel.setMoneySource(0)
                    R.id.partners -> viewModel.setMoneySource(1)
                }
                true
            }
            popupMenu.show()
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
                    .setPositiveButton("Повторить") { dialog, _ ->
                        dialog.dismiss()
                    }
                val alert = dialogBuilder.create()
                alert.setTitle("Вы не выбрали карту перевода!")
                alert.show()
            } else if (binding.withDrawAmount.text.toString() == "") {
                val dialogBuilder = AlertDialog.Builder(this.requireContext())
                dialogBuilder.setMessage("Минимальная сумма перевода 200 \u20b8")
                    .setCancelable(false)
                    .setPositiveButton(
                        "Повторить"
                    ) { dialog, _ ->
                        dialog.dismiss()
                    }
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
                            "Повторить"
                        ) { dialog, _ ->
                            dialog.dismiss()
                        }
                    val alert = dialogBuilder.create()
                    alert.setTitle("Вывод средств невозможен")
                    alert.show()
                } else {
                    val dialogBuilder = AlertDialog.Builder(this.requireContext())
                    dialogBuilder.setMessage("Минимальная сумма перевода 200 \u20b8")
                        .setCancelable(false)
                        .setPositiveButton(
                            "Повторить"
                        ) { dialog, _ ->
                            dialog.dismiss()
                        }
                    val alert = dialogBuilder.create()
                    alert.setTitle("Вывод средств невозможен")
                    alert.show()
                }
            } else if (binding.withDrawAmount.text.toString().toInt() == withDrawAmount.toInt()) {
                val dialogBuilder = AlertDialog.Builder(this.requireContext())
                dialogBuilder.setMessage("На вашем балансе должно оставаться не менее 100 тг, чтобы вы могли получать \"наличные\" заказы.")
                    .setCancelable(false)
                    .setPositiveButton("Повторить") { dialog, _ ->
                        dialog.dismiss()
                    }
                val alert = dialogBuilder.create()
                alert.setTitle("Вывод средств невозможен")
                alert.show()
            } else if (binding.withDrawAmount.text.toString().toInt() > withDrawAmount.toInt()) {
                val dialogBuilder = AlertDialog.Builder(this.requireContext())
                dialogBuilder.setMessage("Не достаточно средств")
                    .setCancelable(false)
                    .setPositiveButton("Повторить") { dialog, _ ->
                        dialog.dismiss()
                    }
                val alert = dialogBuilder.create()
                alert.setTitle("Вывод средств невозможен")
                alert.show()
            } else if (binding.withDrawAmount.text.toString().toInt() > 199) {
                if (withDrawAmount.toInt() - binding.withDrawAmount.text.toString()
                        .toInt() < 100
                ) {
                    val dialogBuilder = AlertDialog.Builder(this.requireContext())
                    dialogBuilder.setMessage("На вашем балансе должно оставаться не менее 100 тг, чтобы вы могли получать \"наличные\" заказы.")
                        .setCancelable(false)
                        .setPositiveButton(
                            "Повторить"
                        ) { dialog, _ ->
                            dialog.dismiss()
                        }
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
            sharedPreferences.edit().putLong("USER_WITHDRAW_TIME", System.currentTimeMillis())
                .apply()
            count(60000) {}
            sharedPreferences.getString("USER_PHONE_NUMBER", "")?.let { userPhoneNumber ->
                viewModel.withDrawCashViewModelFun(
                    amount = with_draw_amount.text.toString(),
                    cardNumber = choose_card_btn.text.toString(),
                    phone = userPhoneNumber.replace("+", "")
                )
            }
            viewModel.responseWithDraw.observe(viewLifecycleOwner, {
                if (it == true) {
                    viewModel.consumeResult()
                    MaterialAlertDialogBuilder(requireContext())
                        .setMessage(resources.getString(R.string.operation_ok))
                        .setPositiveButton(resources.getString(R.string.accept)) { dialog, _ ->
                            withdraw_btn_withdrawPage.isEnabled = true
                            dialog.dismiss()
                        }
                        .show()
                }
            })
        }
    }

    private fun count(delay: Long, checkYandex: () -> Unit) {
        timer?.cancel()
        Log.d(TAG, "count $delay")
        if (delay < 1000 || delay > 60000) {
            checkYandex.invoke()
            return
        }
        timer = object : CountDownTimer(delay, 1000) {
            override fun onTick(p0: Long) {
                if (::binding.isInitialized) {
                    val sec = p0 / 1000
                    if (sec == 0L) {
                        onFinish()
                    } else {
                        binding.withdrawBtnWithdrawPage.isEnabled = false
                        binding.withdrawDelay.visibility = View.VISIBLE
                        binding.withdrawDelay.text =
                            String.format("Следующий вывод доступен через %d сек.", sec)
                    }

                }
            }

            override fun onFinish() {
                if (::binding.isInitialized) {
                    binding.withdrawBtnWithdrawPage.isEnabled = true
                    binding.withdrawDelay.visibility = View.GONE
                    binding.withdrawDelay.text = ""
                }
                checkYandex.invoke()
            }

        }
        timer?.start()
    }

    private fun handleFrame(fragment: Fragment) {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.addToBackStack(fragment::class.java.simpleName)
        fragmentTransaction.replace(R.id.navHostFragment, fragment, fragment::class.java.simpleName)
            .commit()
    }
}