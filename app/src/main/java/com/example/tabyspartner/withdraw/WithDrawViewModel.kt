package com.example.tabyspartner.withdraw

import android.util.Log
import androidx.lifecycle.ViewModel
import com.denzcoskun.imageslider.models.SlideModel
import com.example.tabyspartner.model.CreditCard

class WithDrawViewModel : ViewModel() {

    var cardItemsLis = mutableListOf(
        CreditCard("Каспи Голд","4405 1833 7933 1608")
    )


    init {
        Log.i("WithDrawViewModel","WithDrawViewModel called")
    }

    override fun onCleared() {
        super.onCleared()
        Log.i("WithDrawViewModel","WithDrawViewModel destroyed")
    }
}