package com.dataplus.tabyspartner.ui.ui.profile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dataplus.tabyspartner.model.ResultResponse
import com.dataplus.tabyspartner.networking.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel : ViewModel() {

    val responseInvite = MutableLiveData<String>()
    val responseNews = MutableLiveData<ResultResponse<List<OwnNewsResponse>>>()
    val responseIncomes = MutableLiveData<ResultResponse<List<String>>>()

    fun sendInvite(phone: String, ref: String) {
        OwnApi.retrofitService.invite(phone, ref)
            .enqueue(object : Callback<OwnBaseResponse> {
                override fun onResponse(
                    call: Call<OwnBaseResponse>,
                    response: Response<OwnBaseResponse>
                ) {
                    val resp = response.body()
                    responseInvite.postValue(resp?.success ?: resp?.error)
                }

                override fun onFailure(call: Call<OwnBaseResponse>, t: Throwable) {
                    responseInvite.postValue(t.toString())
                }
            })
    }

    fun getNews() {
        OwnApi.retrofitService.getNews()
            .enqueue(object : Callback<List<OwnNewsResponse>> {
                override fun onResponse(
                    call: Call<List<OwnNewsResponse>>,
                    response: Response<List<OwnNewsResponse>>
                ) {
                    val resp = response.body()
                    responseNews.postValue(ResultResponse.Success(resp ?: listOf()))
                }

                override fun onFailure(call: Call<List<OwnNewsResponse>>, t: Throwable) {
                    responseNews.postValue(ResultResponse.Error(t.toString()))
                }
            })
    }

    fun getIncomes(phone: String) {
        OwnApi.retrofitService.getIncome(phone)
            .enqueue(object : Callback<OwnBaseResponse> {
                override fun onResponse(
                    call: Call<OwnBaseResponse>,
                    response: Response<OwnBaseResponse>
                ) {
                    val resp = response.body()
                    responseIncomes.postValue(ResultResponse.Success(listOfNotNull(resp?.success)))
                }

                override fun onFailure(call: Call<OwnBaseResponse>, t: Throwable) {
                    responseIncomes.postValue(ResultResponse.Error(t.toString()))
                }
            })
    }

}