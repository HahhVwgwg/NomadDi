package com.example.tabyspartner.modal


import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tabyspartner.R
import com.example.tabyspartner.adapter.CreditCardAdapter
import com.example.tabyspartner.databinding.FragmentModalBottomSheetBinding
import com.example.tabyspartner.model.CreditCard
import com.example.tabyspartner.utils.DatabaseHandler
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import kotlinx.android.synthetic.main.fragment_modal_bottom_sheet.*

class ModalBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentModalBottomSheetBinding
    private lateinit var myDb: DatabaseHandler
    private var creditCardListFromDB: MutableList<CreditCard> = mutableListOf()

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_modal_bottom_sheet, container, false
        )

        myDb = DatabaseHandler(requireContext())
        creditCardListFromDB = (myDb.getAllCreditCards() as MutableList<CreditCard>?)!!

        binding.creditCardListModal.adapter = CreditCardAdapter(
            creditCardListFromDB,
            onItemClick = {
//                Toast.makeText(requireContext(),it.creditCardNumber.toString(), Toast.LENGTH_SHORT).show()
//                binding.chooseCardBtn.text = it.creditCardNumber
//                bottomSheetDialog.dismiss()
            },
            myDb,
            this.context,
        )
        binding.creditCardListModal.layoutManager = LinearLayoutManager(
            requireContext()
        )

        binding.addCardBtn.setOnClickListener {

        }
        return binding.root
    }
}