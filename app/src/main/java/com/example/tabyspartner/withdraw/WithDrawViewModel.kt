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

    private val _response = MutableLiveData<CategoryResponse>()

    // The external immutable LiveData for the request status String
    val response: LiveData<CategoryResponse>
        get() = _response

    var cardItemsLis = mutableListOf(
        CreditCard("Каспи Голд","4405 1833 7933 1608")
    )

    init {
        Log.i("WithDrawViewModel","WithDrawViewModel called")
//        getYandexTransactionCategories()
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("WithDrawViewModel","WithDrawViewModel destroyed")
    }


    fun getYandexTransactionCategories() {

        val parkId = "2e8584835dd64db99482b4b21f62a2ae"
        val request = CategoryRequest(
                query = CategoryRequest.Query(
                        category = CategoryRequest.Query.Category(
                                true,
                                true,
                                true,
                                true
                        ),
                        park = CategoryRequest.Query.Park(parkId)
                )
        )


        YandexApi.retrofitService.getCategories(request).enqueue(object : Callback<CategoryResponse> {
            override fun onResponse(call: Call<CategoryResponse>, response: Response<CategoryResponse>) {
                Log.d("Yandex",response.body().toString())

//                for (i in response.body()!!.categories.indices){
//                    response.body()!!.categories[i].is_affecting_driver_balance = true
//                    response.body()!!.categories[i].is_creatable = true
//                    response.body()!!.categories[i].is_editable = true
//                    response.body()!!.categories[i].is_enabled = true
//                }

            }

            override fun onFailure(call: Call<CategoryResponse>, t: Throwable) {
                Log.d("Yandex",t.message.toString())
            }
        })

    }

}