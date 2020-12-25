package com.example.tabyspartner.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.denzcoskun.imageslider.ImageSlider
import com.example.tabyspartner.R
import com.example.tabyspartner.databinding.FragmentMainPageBinding

class MainPageFragment : Fragment() {

    private lateinit var sliderView: ImageSlider

    private val viewModel: MainPageViewModel by lazy {
        ViewModelProvider(this).get(MainPageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding = FragmentMainPageBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        sliderView = binding.imageSlider
        sliderView.setImageList(viewModel.slideModelsList)
        Log.d("Yandex",viewModel.response.value.toString())
        return binding.root
    }
}