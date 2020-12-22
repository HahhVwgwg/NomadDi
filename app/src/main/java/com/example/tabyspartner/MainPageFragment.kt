package com.example.tabyspartner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.denzcoskun.imageslider.ImageSlider
import com.denzcoskun.imageslider.models.SlideModel
import com.example.tabyspartner.databinding.FragmentMainPageBinding

class MainPageFragment : Fragment() {

    private lateinit var sliderView: ImageSlider



    private var slideModelsList = mutableListOf(
        SlideModel("https://images.pexels.com/photos/6190993/pexels-photo-6190993.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940",""),
        SlideModel("https://images.pexels.com/photos/3178852/pexels-photo-3178852.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500",""),
        SlideModel("https://images.pexels.com/photos/5696873/pexels-photo-5696873.jpeg?auto=compress&cs=tinysrgb&dpr=2&w=500",""),
        SlideModel("https://images.pexels.com/photos/6191948/pexels-photo-6191948.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=650&w=940","")
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentMainPageBinding = DataBindingUtil.inflate(
            inflater,R.layout.fragment_main_page,container,false
        )

        sliderView = binding.imageSlider
        sliderView.setImageList(slideModelsList)

        return binding.root
    }
}