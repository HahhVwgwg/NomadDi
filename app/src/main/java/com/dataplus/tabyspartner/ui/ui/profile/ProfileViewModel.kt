package com.dataplus.tabyspartner.ui.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dataplus.tabyspartner.model.ResultResponse
import com.dataplus.tabyspartner.networking.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class ProfileViewModel : ViewModel() {

    val responseInvite = MutableLiveData<String>()
    val responseNews = MutableLiveData<ResultResponse<List<OwnNewsResponse>>>()
    val responseNewsPartners = MutableLiveData<ResultResponse<List<NotificationElement>>>()
    val responseIncomes = MutableLiveData<ResultResponse<List<Pair<String, String>>>>()

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

    private val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error

    fun getNews() {
        OwnApi.retrofitService.getNews()
            .enqueue(object : Callback<List<OwnNewsResponse>> {
                override fun onResponse(
                    call: Call<List<OwnNewsResponse>>,
                    response: Response<List<OwnNewsResponse>>
                ) {
                    val resp = response.body()
                    responseNews.postValue(ResultResponse.Success(resp ?: listOf()))
                }

                override fun onFailure(call: Call<List<OwnNewsResponse>>, t: Throwable) {
                    responseNews.postValue(ResultResponse.Error(t.toString()))
                }
            })
    }

    fun getNewsPartners() {
        APIClient.aPIClient?.getNews()?.enqueue(object : Callback<List<NotificationElement>> {
            override fun onResponse(
                call: Call<List<NotificationElement>>,
                response: Response<List<NotificationElement>>
            ) {
                val res = response.body()
                if (response.isSuccessful ) {
                    val resp = response.body()
                    responseNewsPartners.postValue(ResultResponse.Success(resp ?: listOf()))
//                    responseHistory.postValue(
//                        ResultResponse.Success(
//                            resp?.walletTransation ?: listOf()
//                        )
//                    )
                } else {
//                    _error.postValue(res!!.error)
                }
            }

            override fun onFailure(call: Call<List<NotificationElement>>, t: Throwable) {
                _error.postValue(t.message.toString())
            }
        })
    }


    fun getIncomes(phone: String) {
        OwnApi.retrofitService.getIncome(phone)
            .enqueue(object : Callback<ListResponse<OwnRefResponse>> {
                override fun onResponse(
                    call: Call<ListResponse<OwnRefResponse>>,
                    response: Response<ListResponse<OwnRefResponse>>
                ) {
                    val resp = response.body()
                    val list: MutableList<Pair<String, String>> = mutableListOf()
                    resp?.list?.let { l ->
                        l.forEach { orf ->
                            try {
                                val date = SimpleDateFormat(
                                    "yyyy-MM-dd HH:mm:ss",
                                    Locale.getDefault()
                                ).parse(orf.create_at ?: "")
                                val cal = Calendar.getInstance()
                                cal.time = date ?: Date()
                                val week = cal.get(Calendar.WEEK_OF_YEAR)
                                val year = cal.get(Calendar.YEAR)

                                cal[Calendar.DAY_OF_WEEK] = Calendar.MONDAY
                                cal[Calendar.YEAR] = year
                                cal[Calendar.WEEK_OF_YEAR] = week
                                val monday = cal.time

                                cal[Calendar.DAY_OF_WEEK] = Calendar.SUNDAY
                                cal[Calendar.YEAR] = year
                                cal[Calendar.WEEK_OF_YEAR] = week
                                val sunday = cal.time

                                val sdf = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
                                val period = "${sdf.format(monday)}-${sdf.format(sunday)}"
                                list.add(Pair(period, orf.summa ?: ""))
                            } catch (e: Exception) {

                            }
                        }
                    }
                    val list2 = mutableMapOf<String, String>()
                    list.forEach {
                        val curr = list2[it.first]
                        if (curr == null) {
                            list2[it.first] = it.second
                        } else {
                            list2[it.first] = plus(curr, it.second)
                        }
                    }
                    responseIncomes.postValue(ResultResponse.Success(list2.toList()))
                }

                override fun onFailure(call: Call<ListResponse<OwnRefResponse>>, t: Throwable) {
                    responseIncomes.postValue(ResultResponse.Error(t.toString()))
                }
            })
    }

    private fun plus(old: String?, new: String?): String {
        val oldInt = old?.toInt() ?: 0
        val newInt = new?.toInt() ?: 0
        return (oldInt + newInt).toString()
    }

}