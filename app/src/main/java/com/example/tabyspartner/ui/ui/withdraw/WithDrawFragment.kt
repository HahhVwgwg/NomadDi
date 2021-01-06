package com.example.tabyspartner.ui.ui.withdraw

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
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat.recreate
import androidx.core.text.isDigitsOnly
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
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
import com.example.tabyspartner.utils.DatabaseHandlerHistory
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class WithDrawFragment : Fragment() {

    private lateinit var binding: FragmentWithDrawBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var bottomSheetView : View
    private lateinit var viewModel : WithDrawViewModel
    private lateinit var bindingMainPageBinding: FragmentMainPageBinding
    private lateinit var recyclerView: RecyclerView
    lateinit var sharedPreferences : SharedPreferences
    private lateinit var myDb : DatabaseHandler
    private var cardAdapter: CreditCardAdapter? = null
    private  var creditCardListFromDB :  MutableList<CreditCard> = mutableListOf()
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
        creditCardListFromDB = (myDb.getAllCreditCards() as MutableList<CreditCard>?)!!
        bottomSheetView.findViewById<RecyclerView>(R.id.credit_card_list).adapter = CreditCardAdapter(
            creditCardListFromDB,
            onItemClick = {
                binding.chooseCardBtn.text = it.creditCardNumber
                bottomSheetDialog.dismiss()
            },
            myDb,
            this.context
        )


        bottomSheetView.findViewById<RecyclerView>(R.id.credit_card_list).layoutManager = LinearLayoutManager(
            requireContext()
        )
        viewModel.getYandexDriversProperties(userPhoneNumber!!)
        viewModel.responseD.observe(viewLifecycleOwner, Observer {
            //Log.d("responseDriver", it.toString())
            binding.balanceAmountWithDrawPage.text =
                it.accounts[0].balance.toDouble().toInt().toString() + " \u20b8"
        })

        return binding.root
    }




    @RequiresApi(Build.VERSION_CODES.P)
    override fun onResume() {
        super.onResume()
        creditCardListFromDB = (myDb.getAllCreditCards() as MutableList<CreditCard>?)!!
        binding.withDrawAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(binding.withDrawAmount.text.toString().length > 4) {
                    val calculateFee = (binding.withDrawAmount.text.toString().toDouble() * (0.015)).toInt().toString()
                    binding.amountFee.text =
                        "Комиссия ${calculateFee} ₸"
                    binding.withdrawBtnWithdrawPage.text =
                        "Перевести ${binding.withDrawAmount.text.toString().toInt() - calculateFee.toInt()} \u20b8"
                }else {
                    binding.amountFee.text =
                        "Комиссия 150 ₸"
                    binding.withdrawBtnWithdrawPage.text =
                        "Перевести ${binding.withDrawAmount.text} \u20b8"
                }

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
//            val db = DatabaseHandlerHistory(context)
//            db.deleteAllData()
            if(binding.withDrawAmount.text.toString().toInt() < 251){
                val dialogBuilder = AlertDialog.Builder(this.requireContext())
                // set message of alert dialog
                dialogBuilder.setMessage("Для вывода средств баланс должен быть не менее 251 \u20b8")
                    // if the dialog is cancelable
                    .setCancelable(false)
                    // positive button text and action
                    .setPositiveButton("Повторить", DialogInterface.OnClickListener { dialog, id ->
                       dialog.dismiss()
                    })
                // create dialog box
                val alert = dialogBuilder.create()
                // set title for alert dialog box
                alert.setTitle("Вывод средств невозможен")
                // show alert dialog
                alert.show()
            }else if(!binding.chooseCardBtn.text.toString().isDigitsOnly()) {
                val dialogBuilder = AlertDialog.Builder(this.requireContext())
                // set message of alert dialog

                dialogBuilder.setMessage("Пожалуйста, выберите вашу карту")
                    // if the dialog is cancelable
                    .setCancelable(false)
                    // positive button text and action
                    .setPositiveButton("Повторить", DialogInterface.OnClickListener { dialog, id ->
                        dialog.dismiss()
                    })
                // create dialog box
                val alert = dialogBuilder.create()
                // set title for alert dialog box
                alert.setTitle("Вы не выбрали карту перевода!")
                // show alert dialog
                alert.show()
            }else {
                //viewModel.withdrawCash(binding.withDrawAmount.text.toString(),binding.chooseCardBtn.text.toString(),this.requireContext())
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode==Activity.RESULT_OK) {

            myDb = DatabaseHandler(requireContext())
            creditCardListFromDB = (myDb.getAllCreditCards() as MutableList<CreditCard>?)!!
            bottomSheetView.findViewById<RecyclerView>(R.id.credit_card_list).adapter = CreditCardAdapter(
                creditCardListFromDB,
                onItemClick = {
                    Toast.makeText(requireContext(),it.creditCardNumber.toString(),Toast.LENGTH_SHORT).show()
                    binding.chooseCardBtn.text = it.creditCardNumber
                    bottomSheetDialog.dismiss()
                },
                myDb,
                this.context
            )
            bottomSheetView.findViewById<RecyclerView>(R.id.credit_card_list).layoutManager = LinearLayoutManager(
                requireContext()
            )
        }
    }
}