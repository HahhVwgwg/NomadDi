package com.example.tabyspartner.main

import android.content.Context
import android.util.Log
import android.widget.Toast
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
import kotlin.coroutines.coroutineContext


class AuthorizationViewModel : ViewModel() {
        private val _responseDriver = MutableLiveData<DriverProfilesItem>()
    val response: LiveData<DriverProfilesItem>
        get() = _responseDriver
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

    public fun getUser(context: Context,phone:String,random:String) {

        val parkId = "2e8584835dd64db99482b4b21f62a2ae"
        val randomOTP  = Otp()
        val request = GetSomethingRequest(
                query = GetSomethingRequest.Query(
                        park = GetSomethingRequest.Query.Park(parkId)
                )
        )


        YandexApi.retrofitService.getUser(request).enqueue(object : Callback<DriverProfilesResponse>{
            override fun onResponse(call: Call<DriverProfilesResponse>, response: Response<DriverProfilesResponse>) {
                //Log.d("Yandex",response.body()!!.driversList.toString())

                for (i in response.body()!!.driversList.indices){
                    if(response.body()!!.driversList[i].driver_profile.phones[0]==phone) {
                        _responseDriver.value = response.body()!!.driversList[i]
                        Toast.makeText(context,"Ваш код : ${random}",Toast.LENGTH_LONG).show()
                        //Log.d("Yandex",response.body()!!.driversList[i].driver_profile.first_name)
                    }

                }
            }

            override fun onFailure(call: Call<DriverProfilesResponse>, t: Throwable) {
                Log.d("Yandex",t.message.toString())
            }
        })

    }
}