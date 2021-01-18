package com.dataplus.tabyspartner.main

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dataplus.tabyspartner.networking.*
import com.dataplus.tabyspartner.ui.ui.authorization.Authorization
import com.dataplus.tabyspartner.ui.ui.otp.Otp
import kotlinx.android.synthetic.main.activity_authorization.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AuthorizationViewModel : ViewModel() {

    private val _responseDriver = MutableLiveData<DriverProfilesItem>()
    val response: LiveData<DriverProfilesItem>
        get() = _responseDriver


    private val _response = MutableLiveData<MobizonResponse>()
    val response_mobizon: LiveData<MobizonResponse>
        get() = _response


    private val _responseOtp = MutableLiveData<String>()
    val responseOtp: LiveData<String>
        get() = _responseOtp


    override fun onCleared() {
        super.onCleared()
        Log.i("AuthorizationViewModel", "AuthorizationViewModel destroyed")
    }

    /**
     * Sets the value of the status LiveData to the Yandex status.
     */
    fun getMessageStatus(context: Context,phone:String) {
        val randomOTP  = Otp().OTP(4)
        val apiKey = "kzd22a59d1901e822d4a767ef3bdb90a233d879cdb67be0dff27ecde91897e276ea46d"
        MobizonApi.retrofitService.sendMessage(recipient = phone,text = "Табыс Партнер: Ваш код авторизации: "+randomOTP,apiKey = apiKey).enqueue(object : Callback<MobizonResponse>{
            override fun onResponse(call: Call<MobizonResponse>, response: Response<MobizonResponse>) {
               if(response.isSuccessful) {
                   _response.value = response.body()
                   _responseOtp.value = randomOTP
//                   val intent = Intent(context,MobizonActivity::class.java)
//                   intent.putExtra("verCode",randomOTP)
//                   context.startActivity(intent)
               }
            }

            override fun onFailure(call: Call<MobizonResponse>, t: Throwable) {
                Log.d("Mobizon",t.message.toString())
            }
        })

    }

    fun getUser(phone:String,activity : Authorization) {
        val parkId = "2e8584835dd64db99482b4b21f62a2ae"
        val request = GetSomethingRequest(
                query = GetSomethingRequest.Query(
                        park = GetSomethingRequest.Query.Park(parkId)
                )
        )
        var isFound = false
        YandexApi.retrofitService.getUser(request).enqueue(object : Callback<DriverProfilesResponse>{

            override fun onResponse(call: Call<DriverProfilesResponse>, response: Response<DriverProfilesResponse>) {
                for (i in response.body()!!.driversList.indices){
                    if(response.body()!!.driversList[i].driver_profile.phones[0]==phone) {
                        //Log.d("Yandex",response.body()!!.driversList[i].toString())
                            isFound = true
                        _responseDriver.value = response.body()!!.driversList[i]
                    }
                }
                if(!isFound) {
                    activity.login_form_feedback.visibility = View.VISIBLE
                    activity.login_form_feedback.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    activity.login_form_feedback.text = "Данный номер не зарегистрирован в нашей базе. Попробуйте еще раз."
                }
            }
            override fun onFailure(call: Call<DriverProfilesResponse>, t: Throwable) {
                Log.d("Yandex",t.message.toString())
            }
        })

    }
}