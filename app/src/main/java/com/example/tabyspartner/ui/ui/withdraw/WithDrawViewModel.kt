package com.example.tabyspartner.ui.ui.withdraw

import android.content.Context
import android.os.Build
import android.os.Handler
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tabyspartner.R
import com.example.tabyspartner.networking.*
import com.example.tabyspartner.ui.ui.main.MainPageFragment
import com.example.tabyspartner.ui.ui.otp.Otp
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.fragment_with_draw.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WithDrawViewModel : ViewModel() {

    private val _response = MutableLiveData<BukhtaFeeResponse>()

    // The external immutable LiveData for the request status String
    val response: LiveData<BukhtaFeeResponse>
        get() = _response

    private val _responseD = MutableLiveData<DriverProfilesItem>()

    // The external immutable LiveData for the request status String
    val responseD: LiveData<DriverProfilesItem>
        get() = _responseD


    private val _responseWithDrawYandex = MutableLiveData<WithdrawResponse>()

    // The external immutable LiveData for the request status String
    val responseWithDrawYandex: LiveData<WithdrawResponse>
        get() = _responseWithDrawYandex


    override fun onCleared() {
        super.onCleared()
        Log.i("WithDrawViewModel", "WithDrawViewModel destroyed")
    }

    fun getFee(amount: String, card_number: String) {
        val request = FeeRequest(
            contract_source_id = 24,
            external_ref_id = "1231",
            card_number = card_number,
            amount = amount
        )

        BukhtaApi.retrofitService.calculateFee(request)
            .enqueue(object : Callback<BukhtaFeeResponse> {
                override fun onResponse(
                    call: Call<BukhtaFeeResponse>,
                    response: Response<BukhtaFeeResponse>
                ) {
                    Log.d("Bukhta", response.toString())
                }

                override fun onFailure(call: Call<BukhtaFeeResponse>, t: Throwable) {
                    Log.d("Bukhta", t.message.toString())
                }

            })
    }


    fun withdrawCash(amount: String, card_number: String, context: Context, fragment: Fragment) {
        val externalRefId = Otp().OTP(6)
        val request = FeeRequest(
            contract_source_id = 24,
            external_ref_id = externalRefId,
            card_number = card_number,
            amount = amount
        )

        BukhtaApi.retrofitService.withdrawCash(request)
            .enqueue(object : Callback<BukhtaWithDrawResponse> {
                @RequiresApi(Build.VERSION_CODES.P)
                override fun onResponse(
                    call: Call<BukhtaWithDrawResponse>,
                    response: Response<BukhtaWithDrawResponse>
                ) {
                    Log.d("BukhtaWithDraw", response.body().toString())
                    //fragment.requireActivity().withdraw_btn_withdrawPage.isEnabled = false
                    MaterialAlertDialogBuilder(context)
                        .setIcon(R.drawable.ic_check_bold_24dp)
                        .setMessage(context.resources.getString(R.string.operation_ok))
                        .setPositiveButton(context.resources.getString(R.string.accept)) { dialog, which ->
                            // Respond to positive button press
                            //
                            dialog.dismiss()
                            fragment.requireActivity().withdraw_btn_withdrawPage.isEnabled = true
//                            Handler().postDelayed({
//                                fragment.fragmentManager
//                                    ?.beginTransaction()
//                                    ?.detach(fragment)
//                                    ?.attach(fragment)
//                                    ?.commit();
//                            }, 80000)
                            fragment.fragmentManager?.beginTransaction()
                                ?.replace(R.id.navHostFragment, MainPageFragment())?.commit()
                        }
                        .show()
                }

                override fun onFailure(call: Call<BukhtaWithDrawResponse>, t: Throwable) {
                    Log.d("BukhtaWithDraw", t.message.toString())
                }

            })
    }


    fun getYandexDriversProperties(phone: String) {

        val parkId = "2e8584835dd64db99482b4b21f62a2ae"

        val request = GetSomethingRequest(
            query = GetSomethingRequest.Query(
                park = GetSomethingRequest.Query.Park(parkId)
            )
        )


        YandexApi.retrofitService.getUser(request)
            .enqueue(object : Callback<DriverProfilesResponse> {
                override fun onResponse(
                    call: Call<DriverProfilesResponse>,
                    response: Response<DriverProfilesResponse>
                ) {
                    //Log.d("Yandex",response.body()!!.driversList.toString())

                    for (i in response.body()!!.driversList.indices) {
                        if (response.body()!!.driversList[i].driver_profile.phones[0] == phone) {
                            _responseD.value = response.body()!!.driversList[i]
                            //Log.d("Yandex",response.body()!!.driversList[i].driver_profile.first_name)
                        }

                    }
                }

                override fun onFailure(call: Call<DriverProfilesResponse>, t: Throwable) {
                    Log.d("Yandex", t.message.toString())
                }
            })

    }


    fun withDrawCashFromYandexViewModelFun(driverId: String, amount: String) {
        val parkId = "2e8584835dd64db99482b4b21f62a2ae"
        val request = WithdrawBodyRequest(
            amount = amount,
            category_id = "partner_service_manual",
            description = "Списание",
            driver_profile_id = driverId,
            park_id = parkId
        )

        YandexApi.retrofitService.withDrawCashFromYandex(request)
            .enqueue(object : Callback<WithdrawResponse> {
                override fun onResponse(
                    call: Call<WithdrawResponse>,
                    response: Response<WithdrawResponse>
                ) {
                    _responseWithDrawYandex.value = response.body()
                }

                override fun onFailure(call: Call<WithdrawResponse>, t: Throwable) {
                    Log.d("Error", t.message.toString())
                }

            })

    }

}