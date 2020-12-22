package com.example.tabyspartner.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.denzcoskun.imageslider.ImageSlider
import com.example.tabyspartner.R
import com.example.tabyspartner.databinding.FragmentMainPageBinding

class MainPageFragment : Fragment() {

    private lateinit var sliderView: ImageSlider
    private lateinit var viewModel : MainPageViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val binding: FragmentMainPageBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_main_page,container,false
        )
        Log.i("MainPageFragment","Called ViewModelProviders.of!")
        viewModel = ViewModelProviders.of(this).get(MainPageViewModel::class.java)
        sliderView = binding.imageSlider
        sliderView.setImageList(viewModel.slideModelsList)
        return binding.root
    }
}