package com.example.tabyspartner.ui.ui.withdraw

import android.R.attr
import android.app.Activity
import android.content.Context
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
import android.widget.Button
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tabyspartner.R
import com.example.tabyspartner.adapter.CreditCardAdapter
import com.example.tabyspartner.databinding.FragmentMainPageBinding
import com.example.tabyspartner.databinding.FragmentWithDrawBinding
import com.example.tabyspartner.model.CreditCard
import com.example.tabyspartner.utils.DatabaseHandler
import com.google.android.material.bottomsheet.BottomSheetDialog


class WithDrawFragment : Fragment() {
    private lateinit var binding: FragmentWithDrawBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetView : View
    private lateinit var viewModel : WithDrawViewModel
    private lateinit var bindingMainPageBinding: FragmentMainPageBinding
    lateinit var sharedPreferences : SharedPreferences
    private lateinit var myDb : DatabaseHandler
    private  var creditCardListFromDB :  List<CreditCard> = listOf()
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

        bottomSheetView = LayoutInflater.from(requireContext())
            .inflate(
                R.layout.bottom_sheet_dialog_card_picker,
                requireActivity().findViewById(R.id.bottomSheetContainer)
            )
        myDb = DatabaseHandler(requireContext())
        myDb.deleteAllData()
        creditCardListFromDB = myDb.getAllCreditCards()!!
        bottomSheetView.findViewById<RecyclerView>(R.id.credit_card_list).adapter = CreditCardAdapter(
            creditCardListFromDB,
            onItemClick = {
            })
        bottomSheetView.findViewById<RecyclerView>(R.id.credit_card_list).layoutManager = LinearLayoutManager(
            requireContext()
        )
        viewModel.getYandexDriversProperties(userPhoneNumber!!)
        viewModel.responseD.observe(viewLifecycleOwner, Observer {
            //Log.d("responseDriver", it.toString())
            binding.balanceAmountWithDrawPage.text =
                it.accounts[0].balance.toDouble().toInt().toString() + "\u20b8"
        })
        return binding.root
    }
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
        creditCardListFromDB = myDb.getAllCreditCards()!!
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
        bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        var cardNumber = ""
        bottomSheetView.findViewById<Button>(R.id.button_dialog_sheet).setOnClickListener{
            //binding.chooseCardBtn.text = "**** **** **** ${cardNumber.substring(cardNumber.length-4)}"
            //cardNumber =  bottomSheetView.findViewById<EditText>(R.id.card_number_text).text.toString() //откомментировать
            //binding.chooseCardBtn.text = cardNumber  //откомментировать
            bottomSheetDialog.dismiss()

        }
        bottomSheetView.findViewById<LinearLayout>(R.id.add_card_btn).setOnClickListener {
            //println("Hello From add card btn")
            startActivityForResult(Intent(requireContext(), CardFormActivity::class.java), 1)
        }

        binding.chooseCardBtn.setOnClickListener {
            if (bottomSheetView.getParent() != null) {
                (bottomSheetView.getParent() as ViewGroup).removeView(bottomSheetView) // <- fix
            }
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }
        binding.withdrawBtnWithdrawPage.setOnClickListener {
           // Log.d("BukhtaCheckValid",binding.withDrawAmount.text.toString()+" "+binding.chooseCardBtn.text.toString())
            //viewModel.withdrawCash(binding.withDrawAmount.text.toString(),binding.chooseCardBtn.text.toString())
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK) {
            myDb = DatabaseHandler(requireContext())
            creditCardListFromDB = myDb.getAllCreditCards()!!
            bottomSheetView.findViewById<RecyclerView>(R.id.credit_card_list).adapter = CreditCardAdapter(
                creditCardListFromDB,
                onItemClick = {
                })
            bottomSheetView.findViewById<RecyclerView>(R.id.credit_card_list).layoutManager = LinearLayoutManager(
                requireContext()
            )
        }
    }
}