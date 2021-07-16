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
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dataplus.tabyspartner.MainActivity
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.databinding.FragmentMainPageBinding
import com.dataplus.tabyspartner.databinding.FragmentWithDrawBinding
import com.dataplus.tabyspartner.modal.ModalBottomSheet
import com.dataplus.tabyspartner.networking.CardOtp
import com.dataplus.tabyspartner.ui.ui.authorization.Authorization
import com.dataplus.tabyspartner.ui.ui.pin.VerificationActivity2
import com.dataplus.tabyspartner.utils.SharedHelper
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_with_draw.*

class WithDrawFragment : Fragment() {

    companion object {
        private const val TAG = "WithDrawFragment"
    }

    private var cardId: Int = -1
    private var transactionUrl: String? = null
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
        viewModel.error.observe(viewLifecycleOwner, {
            if (it == null)
                return@observe
            if (it.startsWith("http")) {
                (activity as? MainActivity)?.showBottomSheet(requireContext(), it)
            } else {
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
                if (it == "token_invalid") {
                    sharedPreferences.edit().clear().apply()
                    requireContext().startActivity(
                        Intent(
                            requireContext(),
                            Authorization::class.java
                        )
                    )
                    requireActivity().finish()
                }
            }
            viewModel.consumeError()
        })
        model = activity?.run {
            ViewModelProvider(this).get(ModalBottomSheet.SharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        val cardOtp = CardOtp()
        cardOtp.lastFour = "Выберите карту"
        model.selected.postValue(cardOtp)
        binding.chooseCardBtn.visibility =
            if (SharedHelper.getKey(context, "KASSA", false) && SharedHelper.getKey(
                    context,
                    "CARD_COUNT",
                    0
                ) == 0
            ) View.GONE else View.VISIBLE

        viewModel.profile.observe(viewLifecycleOwner, {
            println("WWS")
            if (it.pending) {
                println("WWS")
                binding.pendingTransaction.visibility = View.VISIBLE
                transactionUrl = it.transactionUrl
            }
        })
        binding.refreshLayout.setOnRefreshListener {
            (activity as MainActivity).handleFrame(WithDrawFragment())
        }
        binding.refresh.setOnClickListener {
            (activity as MainActivity).handleFrame(WithDrawFragment())
        }
        binding.pendingTransaction.setOnClickListener {
            transactionUrl?.let { it1 -> initializeWebView(it1) }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        count(
            10000L - (System.currentTimeMillis() - sharedPreferences.getLong(
                "USER_WITHDRAW_TIME",
                0L
            ))
        ) {
            viewModel.getProfile()
        }
        viewModel.moneySource.observe(viewLifecycleOwner, {
            val balance = viewModel.balance.value
            when (it) {
                0 -> {
                    binding.myBalanceTitle.text = getString(R.string.balance_menu)
                    binding.balanceAmountWithDrawPage.text = String.format(
                        "%s %s",
                        balance?.first ?: "0", "\u20b8"
                    )
                }
                1 -> {
                    binding.myBalanceTitle.text = getString(R.string.partners_menu)
                    binding.balanceAmountWithDrawPage.text = String.format(
                        "%s %s",
                        balance?.second ?: "0", "\u20b8"
                    )
                }
            }
        })
        viewModel.balance.observe(viewLifecycleOwner, Observer {
            it ?: return@Observer
            when (viewModel.moneySource.value) {
                0 -> {
                    binding.balanceAmountWithDrawPage.text = String.format(
                        "%s %s",
                        it.first, "\u20b8"
                    )
                }
                1 -> {
                    binding.balanceAmountWithDrawPage.text = String.format(
                        "%s %s",
                        it.second, "\u20b8"
                    )
                }
            }
        })
        binding.myBalanceHistory.setOnClickListener {
            handleFrame(HistoryFragment.getInstance(viewModel.moneySource.value ?: 0))
        }

        (activity as? MainActivity)?.setToolbarTitle("Вывод средств", false)
        model.selected.observe(viewLifecycleOwner, { item ->
            if (item.lastFour == null) {
                println("WithDrawAmount" + "----" + with_draw_amount.text.toString())
                if (with_draw_amount.text.toString().isNotEmpty()) {
                    val map = HashMap<String, Any>()
                    map["amount"] = with_draw_amount.text.toString()
                    viewModel.addCard(map)
                } else {
                    val dialogBuilder = AlertDialog.Builder(this.requireContext())
                    dialogBuilder.setMessage("Для добавления карты, сперва укажите сумму вывода")
                        .setCancelable(false)
                        .setPositiveButton(
                            "Повторить"
                        ) { dialog, _ ->
                            dialog.dismiss()
                        }
                    val alert = dialogBuilder.create()
                    alert.show()
                }
            } else {
                binding.chooseCardBtn.text = item.lastFour
                cardId = item.id
            }
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
                        "Комиссия 150 ₸"
                    binding.withdrawBtnWithdrawPage.text =
                        "Перевести ${binding.withDrawAmount.text.toString().toInt() - 150} \u20b8"
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        binding.chooseCardBtn.setOnClickListener {
            modalBottomSheetFragment.show(parentFragmentManager, modalBottomSheetFragment.tag)
        }

        viewModel.response.observe(viewLifecycleOwner, {
            binding.webView.visibility = View.VISIBLE
            initializeWebView(it)
        })


        binding.withdrawBtnWithdrawPage.setOnClickListener {
            val withDrawAmount: Int = try {
                binding.balanceAmountWithDrawPage.text.substring(
                    0,
                    binding.balanceAmountWithDrawPage.text.indexOf(".")
                ).toInt()
            } catch (e: StringIndexOutOfBoundsException) {
                binding.balanceAmountWithDrawPage.text.substring(
                    0,
                    binding.balanceAmountWithDrawPage.text.length - 2
                ).toInt()
            }

            if (binding.withDrawAmount.text.toString() == "") {
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
                if (withDrawAmount - binding.withDrawAmount.text.toString().toInt() < 100) {
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
            } else if (binding.withDrawAmount.text.toString().toInt() == withDrawAmount) {
                val dialogBuilder = AlertDialog.Builder(this.requireContext())
                dialogBuilder.setMessage("На вашем балансе должно оставаться не менее 100 тг, чтобы вы могли получать \"наличные\" заказы.")
                    .setCancelable(false)
                    .setPositiveButton("Повторить") { dialog, _ ->
                        dialog.dismiss()
                    }
                val alert = dialogBuilder.create()
                alert.setTitle("Вывод средств невозможен")
                alert.show()
            } else if (binding.withDrawAmount.text.toString().toInt() > withDrawAmount) {
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
                if (withDrawAmount - binding.withDrawAmount.text.toString()
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
                } else if (choose_card_btn.text == "Выберите карту" && choose_card_btn.visibility == View.VISIBLE) {
                    val dialogBuilder = AlertDialog.Builder(this.requireContext())
                    dialogBuilder.setMessage("Сначала выберите карту")
                        .setCancelable(false)
                        .setPositiveButton(
                            "Повторить"
                        ) { dialog, _ ->
                            dialog.dismiss()
                        }
                    val alert = dialogBuilder.create()
                    alert.show()
                } else {
                    if (SharedHelper.getKey(context, "CARD_COUNT", 0) == 0 && SharedHelper.getKey(
                            context,
                            "KASSA",
                            false
                        )
                    ) {
                        val map = HashMap<String, Any>()
                        map["amount"] = with_draw_amount.text.toString()
                        viewModel.addCard(map)
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
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initializeWebView(url: String) {
        val mWebView: WebView = binding.webView
        mWebView.visibility = View.VISIBLE
        webView.clearCache(true)
        mWebView.loadUrl(url)

        // Enable Javascript

        // Enable Javascript
        val webSettings: WebSettings = mWebView.settings
        webSettings.javaScriptEnabled = true

        // Force links and redirects to open in the WebView instead of in a browser
        val webViewClient: WebViewClient = object : WebViewClient() {
            override fun doUpdateVisitedHistory(
                view: WebView, url: String,
                isReload: Boolean
            ) {
                Log.e("TAG", "doUpdateVisited History: " + view.url)
            }

            override fun onLoadResource(view: WebView, url: String) {
                Log.e("TAG", "onLoadResource: " + view.url)
                if (view.url == "https://my.partners-go.kz/api/kassa/callback/android.php?status=ok" || view.url == "https://my.partners-go.kz/api/kassa/callback/android.php") {
                    mWebView.visibility = View.GONE
                    mWebView.destroy()
                }
            }
        }
        // Force links and redirects to open in the WebView instead of in a browser
        mWebView.webViewClient = webViewClient

    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            sharedPreferences.edit().putLong("USER_WITHDRAW_TIME", System.currentTimeMillis())
                .apply()
            count(10000) {}
            val map = HashMap<String, Any>()
            map["card_id"] = cardId
            map["amount"] = with_draw_amount.text.toString()
            viewModel.withdraw(map)
            viewModel.responseWithDraw.observe(viewLifecycleOwner, {
                if (it == true) {
                    viewModel.getProfile()
                    viewModel.consumeResult()
                    binding.amountFee.text = ""
                    with_draw_amount.text.clear()
                    with_draw_amount.text = Editable.Factory.getInstance().newEditable("0")
                    binding.withdrawBtnWithdrawPage.text =
                        "Перевести 0 \u20b8"
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
        if (delay < 1000 || delay > 10000) {
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