package com.example.tabyspartner.ui.ui.main

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.denzcoskun.imageslider.ImageSlider
import com.example.tabyspartner.R
import com.example.tabyspartner.adapter.OnBoardingAdapter
import com.example.tabyspartner.databinding.FragmentMainPageBinding
import com.example.tabyspartner.prefs.PreferencesManager
import com.example.tabyspartner.ui.ui.profile.ProfileFragment
import com.example.tabyspartner.ui.ui.withdraw.WithDrawFragment

class MainPageFragment : Fragment() {

   // private lateinit var sliderView: ImageSlider
   companion object {
       private val TAG = MainPageFragment::class.java
   }

    private var onBoardingPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
//            updateCircleMarker(binding, position)
        }
    }

    private lateinit var binding: FragmentMainPageBinding
    private val viewModel: MainPageViewModel by lazy {
        ViewModelProvider(this).get(MainPageViewModel::class.java)
    }
    lateinit var sharedPreferences : SharedPreferences
    var name_short = ""
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        binding = FragmentMainPageBinding.inflate(inflater)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
       // sliderView = binding.imageSlider
       // sliderView.setImageList(viewModel.slideModelsList)
        val onBoardingAdapter = OnBoardingAdapter(requireActivity() as AppCompatActivity, 1)
        binding.onBoardingViewPager.adapter = onBoardingAdapter
        binding.onBoardingViewPager.registerOnPageChangeCallback(onBoardingPageChangeCallback)

        sharedPreferences = context?.getSharedPreferences("app_prefs",Context.MODE_PRIVATE)!!
        name_short = sharedPreferences.getString("USER_SHORT_NAME", "")!!
        val userPhoneNumber = sharedPreferences.getString("USER_PHONE_NUMBER", "")
        viewModel.getYandexDriversProperties(userPhoneNumber!!)
        viewModel.response.observe(viewLifecycleOwner, Observer {
            //Log.d("responseDriver", it.toString())
            binding.profileNameLabel.text = it.driver_profile.first_name + "\n" + it.driver_profile.last_name
            binding.amountCashNameLabel.text = it.accounts[0].balance
            sharedPreferences.edit()
                .putString("USER_SHORT_NAME", "${it.driver_profile.first_name + " " + it.driver_profile.last_name[0]+"."}")
                .apply()
        })
        return binding.root
    }

    private fun handleFrame(fragment: Fragment): Boolean {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.navHostFragment, fragment).commit()
        return true
    }

    override fun onResume() {
        super.onResume()
        binding.profileInfoBtn.setOnClickListener {
            handleFrame(ProfileFragment())
        }
        binding.balanceInfoBtn.setOnClickListener {
            handleFrame(WithDrawFragment())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.onBoardingViewPager.unregisterOnPageChangeCallback(onBoardingPageChangeCallback)
    }
}