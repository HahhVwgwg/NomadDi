package com.example.tabyspartner.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.denzcoskun.imageslider.models.SlideModel
import com.example.tabyspartner.networking.*
import com.example.tabyspartner.otp.Otp
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AuthorizationViewModel : ViewModel() {

//    // The internal MutableLiveData String that stores the status of the most recent request
//    private val _response = MutableLiveData<MobizonResponse>()
//    private var _recipient :String = ""
//
//    // The external immutable LiveData for the request status String
//    val response: LiveData<MobizonResponse>
//        get() = _response
//
//
//    fun setRecipient(result: String){
//        _recipient = result
//    }
//
//    init {
//        getMessageStatus()
//    }
//
//    override fun onCleared() {
//        super.onCleared()
//        Log.i("AuthorizationViewModel", "AuthorizationViewModel destroyed")
//    }
//
//    /**
//     * Sets the value of the status LiveData to the Yandex status.
//     */
//    private fun getMessageStatus() {
//        val randomOTP  = Otp()
//        val apiKey = "kzd22a59d1901e822d4a767ef3bdb90a233d879cdb67be0dff27ecde91897e276ea46d"
//        println(_recipient)
//        MobizonApi.retrofitService.sendMessage(recipient = _recipient,text = "Ваш код подтверждения: "+randomOTP.OTP(4),apiKey = apiKey).enqueue(object : Callback<MobizonResponse>{
//            override fun onResponse(call: Call<MobizonResponse>, response: Response<MobizonResponse>) {
//                Log.d("Mobizon",response.body().toString())
//            }
//
//            override fun onFailure(call: Call<MobizonResponse>, t: Throwable) {
//                Log.d("Mobizon",t.message.toString())
//            }
//        })
//
//    }
}