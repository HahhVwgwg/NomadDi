package com.dataplus.tabyspartner.ui.ui.authorization

import android.content.Context
import android.util.Log
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dataplus.tabyspartner.networking.*
import com.dataplus.tabyspartner.ui.ui.otp.Otp
import com.dataplus.tabyspartner.utils.SharedHelper
import kotlinx.android.synthetic.main.activity_authorization.*
import kotlinx.android.synthetic.main.activity_mobizon.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*
import kotlin.collections.HashMap


class AuthorizationViewModel : ViewModel() {

    private val _responseDriver = MutableLiveData<OtpResponse>()
    val response: LiveData<OtpResponse>
        get() = _responseDriver


    private val _response = MutableLiveData<MobizonResponse>()
    val response_mobizon: LiveData<MobizonResponse>
        get() = _response


    private val _responseOtp = MutableLiveData<String>()
    val responseOtp: LiveData<String>
        get() = _responseOtp

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error


    override fun onCleared() {
        super.onCleared()
        Log.i("AuthorizationViewModel", "AuthorizationViewModel destroyed")
    }

    /**
     * Sets the value of the status LiveData to the Yandex status.
     */
    var randomOTP = Otp().OTP(4)
    fun getMessageStatus(context: Context, phone: String, otp: String?) {
        val apiKey = "kzd22a59d1901e822d4a767ef3bdb90a233d879cdb67be0dff27ecde91897e276ea46d"
        randomOTP = otp!!
        MobizonApi.retrofitService.sendMessage(
            recipient = phone,
            text = "Табыс Партнер: Ваш код авторизации: " + randomOTP,
            apiKey = apiKey
        ).enqueue(object : Callback<MobizonResponse> {
            override fun onResponse(
                call: Call<MobizonResponse>,
                response: Response<MobizonResponse>
            ) {
//               if(response.isSuccessful) {
//                   //_response.value = response.body()
//
//               }
                _responseOtp.value = randomOTP
            }

            override fun onFailure(call: Call<MobizonResponse>, t: Throwable) {
                Log.d("Mobizon", t.message.toString())
                _responseOtp.value = randomOTP
            }
        })

    }

    fun getUser(phone: String) {
        val hashMap = HashMap<String, Any>()
        hashMap["mobile"] = phone
        APIClient.aPIClient?.getUser(hashMap)?.enqueue(object : Callback<OtpResponse> {
            override fun onResponse(call: Call<OtpResponse>, response: Response<OtpResponse>) {
                val res = response.body()
                if (response.isSuccessful && res?.error.isNullOrEmpty()) {
                    _responseDriver.value = response.body()
                } else {
                    _error.postValue(res!!.error)
                }
            }

            override fun onFailure(call: Call<OtpResponse>, t: Throwable) {
                _error.postValue(t.message.toString())
            }
        })
    }

    fun getUser(phone: String, activity: Authorization) {
        val hashMap = HashMap<String, Any>()
        hashMap["mobile"] = phone
        APIClient.aPIClient?.getUser(hashMap)?.enqueue(object : Callback<OtpResponse> {
            override fun onResponse(call: Call<OtpResponse>, response: Response<OtpResponse>) {
                if (response.isSuccessful) {
                    _responseDriver.value = response.body()
                } else {
                    activity.login_form_feedback.visibility = View.VISIBLE
                    activity.login_form_feedback.textAlignment = View.TEXT_ALIGNMENT_CENTER
                    activity.login_form_feedback.text =
                        "Данный номер не зарегистрирован в нашей базе. Попробуйте еще раз."
                    activity.generate_btn.isEnabled = true
                    activity.login_progress_bar.isEnabled = false
                    activity.phone_number_text.text.clear()
                }
            }

            override fun onFailure(call: Call<OtpResponse>, t: Throwable) {
                println("MineMineFailure" + t.localizedMessage + t.message)
            }
        })
    }

    fun loginByOtp(hashMap: HashMap<String, Any>, activity: MobizonActivity) {
        APIClient.aPIClient?.loginByOtp(hashMap)?.enqueue(object : Callback<TokenOtp> {
            override fun onResponse(call: Call<TokenOtp>, response: Response<TokenOtp>) {
                if (response.isSuccessful) {
                    println("device_tokenwws" + response.body().toString())
                    val tokenOtp = response.body()
                    val accessToken = "Bearer " + tokenOtp?.accessToken
                    SharedHelper.putKey(activity, "access_token", accessToken)
                    activity.redirectToPinCodeActivity()
                } else {
                    Log.d("device_token", "Error")
                }
            }

            override fun onFailure(call: Call<TokenOtp>, t: Throwable) {
                println("MineMineFailure" + t.localizedMessage + t.message)
            }
        })
    }

}