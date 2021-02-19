package com.dataplus.tabyspartner.ui.ui.main

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.adapter.SliderAdapter
import com.dataplus.tabyspartner.databinding.FragmentMainPageBinding
import com.dataplus.tabyspartner.model.SliderItem
import com.dataplus.tabyspartner.ui.ui.profile.ProfileFragment
import com.dataplus.tabyspartner.ui.ui.withdraw.WithDrawFragment
import kotlinx.android.synthetic.main.fragment_main_page.*

class MainPageFragment : Fragment() {

    private lateinit var viewPager2: ViewPager2
    var sliderHandler = Handler()

    companion object {
        private val TAG = MainPageFragment::class.java
    }


    //private lateinit var binding: FragmentMainPageBinding
    private val viewModel: MainPageViewModel by lazy {
        ViewModelProvider(this).get(MainPageViewModel::class.java)
    }
    lateinit var sharedPreferences: SharedPreferences
    var name_short = ""
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreate(savedInstanceState)
        val v = inflater.inflate(R.layout.fragment_main_page, container, false)
        // Inflate the layout for this fragment

        viewPager2 = v.findViewById(R.id.on_boarding_view_pager)
        val sliderItems = mutableListOf(
            SliderItem(R.drawable.banner3),
            SliderItem(R.drawable.bannernew)
        ) as ArrayList

        viewPager2.adapter = SliderAdapter(sliderItems, viewPager2)
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.offscreenPageLimit = 2
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(50))
        viewPager2.setPageTransformer(compositePageTransformer)
        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() { // релиз откоммент
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable,2000)
            }
        })
        sharedPreferences = context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)!!
        name_short = sharedPreferences.getString("USER_SHORT_NAME", "")!!
        val userPhoneNumber = sharedPreferences.getString("USER_PHONE_NUMBER", "")
        viewModel.getYandexDriversProperties(userPhoneNumber!!)
        viewModel.response.observe(viewLifecycleOwner, Observer {
            //Log.d("responseDriver", it.toString())
            profile_name_label.text =
                it.driver_profile.first_name + "\n" + it.driver_profile.last_name
            amount_cash_name_label.text = it.accounts[0].balance
            sharedPreferences.edit()
                .putString(
                    "USER_SHORT_NAME",
                    it.driver_profile.first_name + " " + it.driver_profile.last_name[0] + "."
                )
                .apply()
        })
        return v
    }

    private fun handleFrame(fragment: Fragment): Boolean {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.navHostFragment, fragment).commit()
        return true
    }

    override fun onResume() {
        super.onResume()
        //sliderHandler.postDelayed(sliderRunnable,3000) //релиз откоммент
        profile_info_btn.setOnClickListener {
            handleFrame(ProfileFragment())
        }
        balance_info_btn.setOnClickListener {
            handleFrame(WithDrawFragment())
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    var sliderRunnable = Runnable {
         viewPager2.setCurrentItem(viewPager2.currentItem + 1) // откоммент релиз
    }

    override fun onPause() {
        super.onPause()
         sliderHandler.removeCallbacks(sliderRunnable) //откоммент релиз
    }
}