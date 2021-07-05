package com.dataplus.tabyspartner.ui.ui.withdraw

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dataplus.tabyspartner.networking.APIClient
import com.dataplus.tabyspartner.networking.MessageOtp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CardViewModel : ViewModel() {
    private val _response = MutableLiveData<String>()
    val response: LiveData<String>
        get() = _response

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    fun addCard(hashMap: HashMap<String, Any>) {
        APIClient.aPIClient?.addCard(hashMap)?.enqueue(object : Callback<MessageOtp> {
            override fun onResponse(call: Call<MessageOtp>, response: Response<MessageOtp>) {
                if (response.body()?.success != null)
                    _response.value = response.body()?.success
                else if (response.body()?.error != null) {
                    _error.value = response.body()!!.error
                }
            }

            override fun onFailure(call: Call<MessageOtp>, t: Throwable) {
                _error.postValue(t.message.toString())
            }
        })
    }
}