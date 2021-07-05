package com.dataplus.tabyspartner.modal

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.dataplus.tabyspartner.MainActivity
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.adapter.CreditCardAdapter
import com.dataplus.tabyspartner.databinding.FragmentModalBottomSheetBinding
import com.dataplus.tabyspartner.databinding.FragmentWithDrawBinding
import com.dataplus.tabyspartner.model.CreditCard
import com.dataplus.tabyspartner.model.ResultResponse
import com.dataplus.tabyspartner.networking.APIClient
import com.dataplus.tabyspartner.networking.CardOtp
import com.dataplus.tabyspartner.networking.MessageOtp
import com.dataplus.tabyspartner.networking.WalletTransation
import com.dataplus.tabyspartner.ui.ui.profile.adapter.IncomesAdapter
import com.dataplus.tabyspartner.ui.ui.withdraw.CardFormActivity
import com.dataplus.tabyspartner.utils.DatabaseHandler
import com.dataplus.tabyspartner.utils.SharedHelper
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ModalBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentModalBottomSheetBinding
    private lateinit var myDb: DatabaseHandler
    private var creditCardListFromDB: MutableList<CreditCard> = mutableListOf()
    private lateinit var bindingWithDraw: FragmentWithDrawBinding
    private lateinit var model: SharedViewModel
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_modal_bottom_sheet, container, false
        )

        bindingWithDraw = DataBindingUtil.inflate(
            inflater, R.layout.fragment_with_draw, container, false
        )

        myDb = DatabaseHandler(requireContext())
        creditCardListFromDB = (myDb.getAllCreditCards() as MutableList<CreditCard>?)!!

        model = activity?.run {
            ViewModelProvider(this).get(SharedViewModel::class.java)
        } ?: throw Exception("Invalid Activity")
        model.getProviderCard()
        model.responseCardProvider.observe(requireActivity(), {
            when (it) {
                is ResultResponse.Loading -> {
                }
                is ResultResponse.Success -> {
                    if (it.data.isNotEmpty())
                        initAdapter(it.data as MutableList<CardOtp>)
                }
                is ResultResponse.Error -> {
                    Toast.makeText(view?.context, it.message, Toast.LENGTH_SHORT).show()
                }
            }
        })

        binding.creditCardListModal.layoutManager = LinearLayoutManager(
            requireContext()
        )

        binding.addCardBtn.setOnClickListener {
            if (SharedHelper.getKey(context, "KASSA", false)) {
                model.select(CardOtp())
                dismiss()
            } else {
                startActivityForResult(Intent(requireContext(), CardFormActivity::class.java), 1)
            }

        }

        binding.buttonDialogSheet.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun initAdapter(creditCardListFromDB: MutableList<CardOtp>) {
        binding.creditCardListModal.adapter = CreditCardAdapter(
            creditCardListFromDB,
            onItemClick = {
                model.select(it)
                this.dismiss()
            },
            onDelete = {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(context?.resources?.getString(R.string.delete_card_title))
                    .setMessage(context?.resources?.getString(R.string.delete_card_message))
                    .setNegativeButton(context?.resources?.getString(R.string.decline)) { dialog, which ->
                        dialog.dismiss()
                    }
                    .setPositiveButton(context?.resources?.getString(R.string.accept)) { dialog, which ->
                        val hashMap = HashMap<String, Any>()
                        hashMap["id"] = it.id;
                        model.deleteCard(hashMap)
                        model.response.observe(viewLifecycleOwner, {
                            if (it) {
                                fragmentManager
                                    ?.beginTransaction()
                                    ?.detach(this)
                                    ?.attach(this)
                                    ?.commit();
                            } else {
                                Toast.makeText(context, "Что-то пошло не так", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                    .show()
            },
            myDb,
            this.context,
        )
    }

    class SharedViewModel : ViewModel() {
        val selected = MutableLiveData<CardOtp>()

        fun select(item: CardOtp) {
            selected.value = item
        }

        val responseCardProvider = MutableLiveData<ResultResponse<List<CardOtp>>>()

        private val _response = MutableLiveData<Boolean>()
        val response: LiveData<Boolean>
            get() = _response

        private val _error = MutableLiveData<String>()
        val error: LiveData<String>
            get() = _error

        fun getProviderCard() {
            APIClient.aPIClient?.getProviderCard()?.enqueue(object : Callback<List<CardOtp>> {
                override fun onResponse(call: Call<List<CardOtp>>, response: Response<List<CardOtp>>) {
                    if (response.isSuccessful) {
                        val resp = response.body()
                        responseCardProvider.postValue(ResultResponse.Success(resp?.toList() ?: listOf()))
                    } else {
                        Log.d("device_token", "Error")
                    }
                }

                override fun onFailure(call: Call<List<CardOtp>>, t: Throwable) {
                    println("MineMineFailure" + t.localizedMessage + t.message)
                }
            })
        }

        fun deleteCard(hashMap: HashMap<String, Any>) {
            APIClient.aPIClient?.deleteCard(hashMap)?.enqueue(object : Callback<MessageOtp> {
                override fun onResponse(call: Call<MessageOtp>, response: Response<MessageOtp>) {
                    if (response.isSuccessful) {
                        _response.value = response.body()?.message == "Card deleted"
                    } else {
                        _response.value = false
                    }
                }

                override fun onFailure(call: Call<MessageOtp>, t: Throwable) {
                    _response.value = false
                }
            })
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            fragmentManager
                ?.beginTransaction()
                ?.detach(this)
                ?.attach(this)
                ?.commit();
        }
    }

}