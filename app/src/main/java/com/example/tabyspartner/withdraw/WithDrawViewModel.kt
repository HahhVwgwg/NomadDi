package com.example.tabyspartner.withdraw

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.denzcoskun.imageslider.models.SlideModel
import com.example.tabyspartner.model.CreditCard
import com.example.tabyspartner.networking.*
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

    var cardItemsLis = mutableListOf(
        CreditCard("Каспи Голд", "4405 1833 7933 1608")
    )

    init {
        Log.i("WithDrawViewModel", "WithDrawViewModel called")
//        getYandexTransactionCategories()
    }

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


    fun withdrawCash(amount: String, card_number: String) {
        val request = FeeRequest(
            contract_source_id = 24,
            external_ref_id = "7369",
            card_number = card_number,
            amount = amount
        )

        BukhtaApi.retrofitService.withdrawCash(request)
            .enqueue(object : Callback<BukhtaWithDrawResponse> {
                override fun onResponse(
                    call: Call<BukhtaWithDrawResponse>,
                    response: Response<BukhtaWithDrawResponse>
                ) {
                    Log.d("BukhtaWithDraw", response.body().toString())
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

}