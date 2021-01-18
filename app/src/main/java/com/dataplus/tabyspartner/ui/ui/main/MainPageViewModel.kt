package com.dataplus.tabyspartner.ui.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.denzcoskun.imageslider.models.SlideModel
import com.dataplus.tabyspartner.networking.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainPageViewModel : ViewModel() {

    // The internal MutableLiveData String that stores the status of the most recent request
    private val _response = MutableLiveData<DriverProfilesItem>()

    // The external immutable LiveData for the request status String
    val response: LiveData<DriverProfilesItem>
        get() = _response

    /**
     * Call getYandexDriversProperties() on init so we can display status immediately.
     */
    var slideModelsList = mutableListOf(
        SlideModel(
            "https://images.pexels.com/photos/6190993/pexels-photo-6190993.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940",
            ""
        ),
        SlideModel(
            "https://images.pexels.com/photos/3178852/pexels-photo-3178852.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500",
            ""
        ),
        SlideModel(
            "https://images.pexels.com/photos/5696873/pexels-photo-5696873.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500",
            ""
        ),
        SlideModel(
            "https://images.pexels.com/photos/6191948/pexels-photo-6191948.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940",
            ""
        )
    )

    override fun onCleared() {
        super.onCleared()
        Log.i("MainPageViewModel", "MainPageViewModel destroyed")
    }

    /**
     * Sets the value of the status LiveData to the Yandex status.
     */
    fun getYandexDriversProperties(phone:String) {

        val parkId = "2e8584835dd64db99482b4b21f62a2ae"

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
                        _response.value = response.body()!!.driversList[i]
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