package com.example.tabyspartner.withdraw

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.example.tabyspartner.R
import com.example.tabyspartner.databinding.FragmentMainPageBinding
import com.example.tabyspartner.databinding.FragmentProfileBinding
import com.example.tabyspartner.databinding.FragmentWithDrawBinding
import com.example.tabyspartner.main.MainPageViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class WithDrawFragment : Fragment() {
    private lateinit var binding: FragmentWithDrawBinding
    private lateinit var viewModel : WithDrawViewModel
    private lateinit var bindingMainPageBinding: FragmentMainPageBinding

    private val viewModelMainPageViewModel: MainPageViewModel by lazy {
        ViewModelProvider(this).get(MainPageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = DataBindingUtil.inflate(
            inflater,R.layout.fragment_with_draw,container,false
        )
        bindingMainPageBinding = DataBindingUtil.inflate(
                inflater,R.layout.fragment_main_page,container,false
        )
        Log.i("MainPageFragment","Called ViewModelProviders.of!")
        viewModel = ViewModelProviders.of(this).get(WithDrawViewModel::class.java)

        viewModelMainPageViewModel.response.observe(viewLifecycleOwner, Observer {
//          binding.balanceAmountWithDrawPage.text = it.driver_profile.first_name+"\n"+it.driver_profile.last_name
            binding.balanceAmountWithDrawPage.text = it.accounts[0].balance.toDouble().toInt().toString() + " \u20b8"
        })
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        binding.chooseCardBtn.setOnClickListener {
            val bottomSheetDialog = BottomSheetDialog(requireContext(),R.style.BottomSheetDialogTheme)
            val bottomSheetView = LayoutInflater.from(requireContext())
                .inflate(
                    R.layout.bottom_sheet_dialog_card_picker,requireActivity().findViewById(R.id.bottomSheetContainer)
                )
            bottomSheetView.findViewById<Button>(R.id.button_dialog_sheet).setOnClickListener{
                bottomSheetDialog.dismiss()
            }
            bottomSheetDialog.setContentView(bottomSheetView)
            bottomSheetDialog.show()
        }
    }
}