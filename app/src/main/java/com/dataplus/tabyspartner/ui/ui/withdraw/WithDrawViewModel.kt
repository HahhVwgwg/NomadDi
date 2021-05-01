package com.dataplus.tabyspartner.ui.ui.withdraw

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dataplus.tabyspartner.model.ResultResponse
import com.dataplus.tabyspartner.networking.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WithDrawViewModel : ViewModel() {

    private val _balance = MutableLiveData<Pair<String, String>>()
    val balance: LiveData<Pair<String, String>>
        get() = _balance

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
                if (res?.error.isNullOrEmpty()) {
                    _responseWithDraw.postValue(true)
                    getDriversProperties(phone)
                } else {
                    _responseWithDraw.postValue(false)
                    _error.postValue(res?.error)
                }
            }

            override fun onFailure(call: Call<OwnBaseResponse>, t: Throwable) {
                Log.d("Balance", t.message.toString())
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

    fun getHistory(phone: String, mode: Int) {
        val call = if (mode == 0) {
            OwnApi.retrofitService.getWithdrawHistory(phone)
        } else {
            OwnApi.retrofitService.getWithdrawHistoryRef(phone)
        }
        call.enqueue(object : Callback<List<OwnRefResponse>> {
                override fun onResponse(
                    call: Call<List<OwnRefResponse>>,
                    response: Response<List<OwnRefResponse>>
                ) {
                    val resp = response.body()
                   // responseNews.postValue(ResultResponse.Success(resp ?: listOf()))
                }

                override fun onFailure(call: Call<List<OwnRefResponse>>, t: Throwable) {
                   // responseNews.postValue(ResultResponse.Error(t.toString()))
                }
            })
    }

}