package com.dataplus.tabyspartner.ui.ui.main

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.dataplus.tabyspartner.MainActivity
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.adapter.SliderAdapter
import com.dataplus.tabyspartner.databinding.FragmentMainPageBinding
import com.dataplus.tabyspartner.model.SliderItem
import com.dataplus.tabyspartner.ui.ui.authorization.Authorization
import com.dataplus.tabyspartner.utils.SharedHelper

class MainPageFragment : Fragment() {

    private lateinit var viewPager2: ViewPager2
    var sliderHandler = Handler(Looper.getMainLooper())

    companion object {
        private val TAG = MainPageFragment::class.java
    }

    private lateinit var binding: FragmentMainPageBinding

    private var freeze = false

    private val viewModel: MainPageViewModel by lazy {
        ViewModelProvider(this).get(MainPageViewModel::class.java)
    }

    lateinit var sharedPreferences: SharedPreferences

    var name_short = ""

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {


        // Inflate the layout for this fragment
        binding = FragmentMainPageBinding.inflate(inflater)
        binding.lifecycleOwner = this
//        binding.viewModel = viewModel

        viewPager2 = binding.onBoardingViewPager
        val sliderItems = mutableListOf(
            SliderItem(R.drawable.banner3),
            SliderItem(R.drawable.bannernew)
        ) as ArrayList
        viewPager2.adapter =
            SliderAdapter(sliderItems, viewPager2, object : SliderAdapter.Callback {
                override fun onAction(action: Int) {
                    if (action == MotionEvent.ACTION_DOWN) {
                        freeze = true
                    } else if (action == MotionEvent.ACTION_UP) {
                        freeze = false
                        sliderHandler.removeCallbacks(sliderRunnable)
                        sliderHandler.postDelayed(sliderRunnable, 3000)
                    }
                }

            })
        viewPager2.clipToPadding = false
        viewPager2.clipChildren = false
        viewPager2.offscreenPageLimit = 2
        viewPager2.getChildAt(0).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer(50))
        viewPager2.setPageTransformer(compositePageTransformer)
        viewPager2.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() { // релиз откоммент
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                sliderHandler.removeCallbacks(sliderRunnable)
                sliderHandler.postDelayed(sliderRunnable, 3000)
            }
        })
        sharedPreferences = context?.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)!!
        name_short = sharedPreferences.getString("USER_SHORT_NAME", "")!!
        val userPhoneNumber = sharedPreferences.getString("USER_PHONE_NUMBER", "")
        binding.refreshLayout.setOnRefreshListener {
            (activity as MainActivity).handleFrame(MainPageFragment())
        }
        binding.refresh.setOnClickListener {
            (activity as MainActivity).handleFrame(MainPageFragment())
        }

        viewModel.error.observe(viewLifecycleOwner, {
            if (it.startsWith("http")) {
                (activity as? MainActivity)?.showBottomSheet(requireContext(), it)
            } else {
                val dialogBuilder = AlertDialog.Builder(this.requireContext())
                dialogBuilder.setMessage(it)
                    .setCancelable(false)
                    .setPositiveButton(
                        "Повторить"
                    ) { dialog, _ ->
                        dialog.dismiss()
                    }
                val alert = dialogBuilder.create()
                alert.show()
                if (it == "token_invalid") {
                    sharedPreferences.edit().clear().apply()
                    requireContext().startActivity(
                        Intent(
                            requireContext(),
                            Authorization::class.java
                        )
                    )
                    requireActivity().finish()
                }
            }
        })
        viewModel.getProfile()
        viewModel.profileData.observe(viewLifecycleOwner, {
            binding.profileNameLabel.text =
                it.firstName + "\n" + it.lastName
            binding.amountCashNameLabel.text = it.walletBalance.toString()
            sharedPreferences.edit()
                .putString(
                    "USER_SHORT_NAME",
                    it.firstName + " " + it.lastName + "."
                )
                .apply()
            SharedHelper.putKey(context, "KASSA", it.paymentType.equals("KASSA"))
            SharedHelper.putKey(context, "CARD_COUNT", it.cardCount)
        })
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        //sliderHandler.postDelayed(sliderRunnable,3000) //релиз откоммент
        binding.profileInfoBtn.setOnClickListener {
            (activity as? MainActivity)?.open(R.id.profile_menu_item)
        }
        binding.balanceInfoBtn.setOnClickListener {
            (activity as? MainActivity)?.open(R.id.withdraw_menu_item)
        }
    }


    var sliderRunnable = Runnable {
        if (!freeze) {
            viewPager2.currentItem = viewPager2.currentItem + 1 // откоммент релиз
        }
    }

    override fun onPause() {
        super.onPause()
        sliderHandler.removeCallbacks(sliderRunnable) //откоммент релиз
    }
}