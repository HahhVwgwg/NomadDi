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
                if (response.isSuccessful) {
                    if (response.body()?.success != null)
                        _response.value = response.body()?.success
                    else
                        _response.value = response.body()?.error
                } else {
                    _response.value = "            "
                }
            }

            override fun onFailure(call: Call<MessageOtp>, t: Throwable) {
                _response.value = t.localizedMessage + t.message
            }
        })
    }
}