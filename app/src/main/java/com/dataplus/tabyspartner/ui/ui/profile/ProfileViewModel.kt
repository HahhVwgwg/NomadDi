package com.dataplus.tabyspartner.ui.ui.profile

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dataplus.tabyspartner.networking.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileViewModel : ViewModel() {

    val responseInvite = MutableLiveData<String>()

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

}