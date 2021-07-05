package com.dataplus.tabyspartner.modal

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.dataplus.tabyspartner.R
import com.dataplus.tabyspartner.databinding.FragmentUpdateBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class UpdateBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentUpdateBottomSheetBinding


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_update_bottom_sheet, container, false
        )

        dialog?.setCancelable(false)
        dialog?.setCanceledOnTouchOutside(false)
        binding.update.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(arguments?.getString("url")))
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        return binding.root
    }
}