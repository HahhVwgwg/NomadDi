package com.dataplus.tabyspartner.ui.ui.withdraw

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dataplus.tabyspartner.BuildConfig
import com.dataplus.tabyspartner.model.ResultResponse
import com.dataplus.tabyspartner.networking.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WithDrawViewModel : ViewModel() {

    val responseHistory = MutableLiveData<ResultResponse<List<WalletTransation>>>()
    val responseHistoryRef = MutableLiveData<ResultResponse<List<OwnWithdrawResponse>>>()

    private val _balance = MutableLiveData<Pair<String, String>>()
    val balance: LiveData<Pair<String, String>>
        get() = _balance

    private val _response = MutableLiveData<String>()
    val response: LiveData<String>
        get() = _response

    private val _moneySource = MutableLiveData(0)
    val moneySource: LiveData<Int>
        get() = _moneySource

    private val _responseWithDraw = MutableLiveData<Boolean>()
    val responseWithDraw: LiveData<Boolean>
        get() = _responseWithDraw

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    override fun onCleared() {
        super.onCleared()
        Log.i("WithDrawViewModel", "WithDrawViewModel destroyed")
    }

    fun String?.parseSum(): String {
        this ?: return "0"
        return try {
            if (this.toFloat() > 0f) {
                this
            } else {
                "0"
            }
        } catch (e: Exception) {
            "0"
        }
    }

    fun getDriversProperties(phone: String) {
        OwnApi.retrofitService.getBalance(phone)
            .enqueue(object : Callback<OwnBaseResponse> {
                override fun onResponse(
                    call: Call<OwnBaseResponse>,
                    response: Response<OwnBaseResponse>
                ) {
                    val res = response.body()
                    if (res?.error.isNullOrEmpty()) {
                        _balance.postValue(
                            Pair(
                                res?.success.parseSum(),
                                res?.ref_balance.parseSum()
                            )
                        )
                    } else {
                        _balance.postValue(Pair("0", "0"))
                        _error.postValue(res?.error)
                    }
                }

                override fun onFailure(call: Call<OwnBaseResponse>, t: Throwable) {
                    Log.d("Balance", t.message.toString())
                    _error.postValue(t.message.toString())
                }
            })

    }

    fun getProfile() {
        APIClient.aPIClient?.getProfile(BuildConfig.DEVICE_TYPE, BuildConfig.VERSION_NAME)?.enqueue(object : Callback<ProfileOtp> {
            override fun onResponse(call: Call<ProfileOtp>, response: Response<ProfileOtp>) {
                if (response.body()?.error != null) {
                    _error.value = response.body()!!.error
                } else {
                    val profileOtp: ProfileOtp = response.body()!!
                    if (profileOtp.forceUpdate){
                        _error.value = profileOtp.url
                    } else {
                        _balance.postValue(
                            Pair(
                                profileOtp.walletBalance.toString().parseSum(),
                                "0"
                            )
                        )
                    }
                }
            }

            override fun onFailure(call: Call<ProfileOtp>, t: Throwable) {
                _error.postValue(t.message.toString())
            }
        })
    }

    fun withDrawCashViewModelFun(amount: String, cardNumber: String, phone: String) {
        val mode = moneySource.value ?: 0
        val call = if (mode == 0) {
            OwnApi.retrofitService.makeWithdraw(phone, amount, cardNumber)
        } else {
            OwnApi.retrofitService.makeWithdrawRef(phone, amount, cardNumber)
        }
        call.enqueue(object : Callback<OwnBaseResponse> {
            override fun onResponse(
                call: Call<OwnBaseResponse>,
                response: Response<OwnBaseResponse>
            ) {
                val res = response.body()
                if (response.isSuccessful && res?.error.isNullOrEmpty()) {
                    _responseWithDraw.postValue(true)
                    getDriversProperties(phone)
                } else {
                    _responseWithDraw.postValue(false)
                    _error.postValue(res?.error ?: response.errorBody().toString())
                }
            }

            override fun onFailure(call: Call<OwnBaseResponse>, t: Throwable) {
                Log.d("Balance", t.message.toString())
                _error.postValue(t.message.toString())
            }
        })
    }

    fun withdraw(hashMap: HashMap<String, Any>) {
        APIClient.aPIClient?.withdraw(hashMap)?.enqueue(object : Callback<MessageOtp> {
            override fun onResponse(call: Call<MessageOtp>, response: Response<MessageOtp>) {
                val res = response.body()
                if (response.isSuccessful && res?.error.isNullOrEmpty()) {
                    _responseWithDraw.postValue(true)
                } else {
                    _error.postValue(res!!.error)
                }
            }

            override fun onFailure(call: Call<MessageOtp>, t: Throwable) {
                _error.postValue(t.message.toString())
            }
        })
    }

    fun setMoneySource(source: Int) {
        _moneySource.postValue(source)
    }

    fun consumeError() {
        _error.postValue(null)
    }

    fun consumeResult() {
        _responseWithDraw.postValue(null)
    }

    fun getWalletTransaction() {
        APIClient.aPIClient?.getWalletTransaction()?.enqueue(object : Callback<WalletTransactions> {
            override fun onResponse(
                call: Call<WalletTransactions>,
                response: Response<WalletTransactions>
            ) {
                val res = response.body()
                if (response.isSuccessful && res?.error.isNullOrEmpty()) {
                    val resp = response.body()
                    responseHistory.postValue(
                        ResultResponse.Success(
                            resp?.walletTransation ?: listOf()
                        )
                    )
                } else {
                    _error.postValue(res!!.error)
                }
            }

            override fun onFailure(call: Call<WalletTransactions>, t: Throwable) {
                _error.postValue(t.message.toString())
            }
        })
    }

    fun addCard(hashMap: HashMap<String, Any>) {
        APIClient.aPIClient?.addCard(hashMap)?.enqueue(object : Callback<MessageOtp> {
            override fun onResponse(call: Call<MessageOtp>, response: Response<MessageOtp>) {
                val res = response.body()
                if (response.isSuccessful && res?.error.isNullOrEmpty()) {
                    _response.value = response.body()?.url
                    println("wwwwws")
                } else {
                    _error.postValue(res!!.error)
                }
            }

            override fun onFailure(call: Call<MessageOtp>, t: Throwable) {
                _error.postValue(t.message.toString())
            }
        })
    }

//    fun getHistory(phone: String, mode: Int) {
//        val call = if (mode == 0) {
//            OwnApi.retrofitService.getWithdrawHistory(phone)
//        } else {
//            OwnApi.retrofitService.getWithdrawHistoryRef(phone)
//        }
//        call.enqueue(object : Callback<ListResponse<OwnWithdrawResponse>> {
//            override fun onResponse(
//                call: Call<ListResponse<OwnWithdrawResponse>>,
//                response: Response<ListResponse<OwnWithdrawResponse>>
//            ) {
//                val resp = response.body()
//                if (mode == 0) {
//                    responseHistory.postValue(ResultResponse.Success(resp?.list ?: listOf()))
//                } else {
//                    responseHistoryRef.postValue(ResultResponse.Success(resp?.list ?: listOf()))
//                }
//            }
//
//            override fun onFailure(call: Call<ListResponse<OwnWithdrawResponse>>, t: Throwable) {
//                if (mode == 0) {
//                    responseHistory.postValue(ResultResponse.Error(t.toString()))
//                } else {
//                    responseHistoryRef.postValue(ResultResponse.Error(t.toString()))
//                }
//            }
//        })
//    }

}