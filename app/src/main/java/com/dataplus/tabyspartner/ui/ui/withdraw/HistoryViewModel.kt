package com.dataplus.tabyspartner.ui.ui.withdraw

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dataplus.tabyspartner.networking.APIClient
import com.dataplus.tabyspartner.networking.HistoryOtp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistoryViewModel : ViewModel() {
    private val _response = MutableLiveData<HistoryOtp>()
    val response: LiveData<HistoryOtp>
        get() = _response

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    fun getHistoryById(id: Int) {
        APIClient.aPIClient?.getHistoryById(id)?.enqueue(object : Callback<HistoryOtp> {
            override fun onResponse(call: Call<HistoryOtp>, response: Response<HistoryOtp>) {
                if (response.body()?.error != null) {
                    _error.value = response.body()!!.error
                } else {
                    _response.value = response.body()
                }
            }

            override fun onFailure(call: Call<HistoryOtp>, t: Throwable) {
                _error.postValue(t.message.toString())
            }
        })
    }
}