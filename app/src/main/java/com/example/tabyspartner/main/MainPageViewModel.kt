package com.example.tabyspartner.main

import android.util.Log
import androidx.lifecycle.ViewModel
import com.denzcoskun.imageslider.models.SlideModel


class MainPageViewModel : ViewModel() {

    var slideModelsList = mutableListOf(
        SlideModel("https://images.pexels.com/photos/6190993/pexels-photo-6190993.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940",""),
        SlideModel("https://images.pexels.com/photos/3178852/pexels-photo-3178852.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500",""),
        SlideModel("https://images.pexels.com/photos/5696873/pexels-photo-5696873.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500",""),
        SlideModel("https://images.pexels.com/photos/6191948/pexels-photo-6191948.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940","")
    )

    init {
        Log.i("MainPageViewModel","MainPageViewModel called")
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("MainPageViewModel","MainPageViewModel destroyed")
    }
}